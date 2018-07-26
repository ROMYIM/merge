package com.merge.util;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.HttpClientUtil;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.service.AgreementAccountService;
import com.merge.service.StreamService;

public class ShamController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);
    
    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
  
    @Override
    public String baseLogin(int id, String code, String sn, String mac, MongoTemplate mongo) {       
        String flag = "fail", result = "", type = "sham";
        AgreementAccountBean account = new AgreementAccountBean();
        mongoTemplate = mongo;
        try {
            String url = "http://shamtv1.mod92.com/iptv/V5/API-V5.php?mode=active&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
            result = HttpClientUtil.sendHttpGet(url);
            //账号的状态。-1：失败；0：成功； 1：账号不存在； 2：账号被暂停或过期；
            //注意：每次请求的播放链接不一样，因为密码会变。容易请求超时!!!!!
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("status").equals("100")) {
                String user_agent = json.getString("user_agent");
                url = "http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=packages&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                JSONObject packagestr = null;
                if (result.indexOf("FROM CACHE =") != -1) {
                    packagestr = JSONObject.parseObject(result.replace("FROM CACHE =", "").trim());
                }else {
                    packagestr = JSONObject.parseObject(result);
                }
                JSONArray categoryArr = JSONArray.parseArray(packagestr.getString("packages"));
                if (categoryArr.size() > 0) {
                    for (int i=0; i<categoryArr.size(); i++) {
                        JSONObject category = categoryArr.getJSONObject(i);
                        getChannel(category.getString("pkg_channels"), category.getString("pkg_name"), user_agent, code, mac, sn, 0);
                    }
                }
                //movie
                url = "http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=movies_cat&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                JSONArray movieCategoryArr = JSONArray.parseArray(result);
                if (movieCategoryArr.size() > 0) {
                    for (int m=0; m<movieCategoryArr.size(); m++) {
                        JSONObject category = movieCategoryArr.getJSONObject(m);
                        url = "http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=movies_list&code="+code+"&mac="+mac+"&sn="+sn+"&catid="+category.getString("id")+"&raw=yes";
                        result = HttpClientUtil.sendHttpGet(url);
                        if (!result.equals("null")) {
                            getChannel(result, category.getString("category_name"), user_agent, code, mac, sn, 1);
                        }
                    }
                }
                account.setStatus(0);
            }else{ 
                if (json.getString("status").equals("103")) {
                    account.setStatus(1);
                }else if(json.getString("status").equals("102") || json.getString("status").equals("104")) {
                    account.setStatus(2);
                }
                account.setErrorstr(json.getString("message"));
                //当账号无效时，清除playurl
                Criteria c = new Criteria();  
                c.and("code").is(code);
                c.and("mac").is(mac);
                c.and("sn").is(sn);
                c.and("type").is(type);
                List<PlayUrlBean> sclist = mongoTemplate.find(Query.query(c), PlayUrlBean.class); 
                if (sclist != null && sclist.size() > 0) {
                    for (PlayUrlBean sc : sclist) {
                        String streamid = sc.getStreamid();
                        Query q = Query.query(Criteria.where("streamid").is(streamid));
                        List<PlayUrlBean> sidlist = mongoTemplate.find(q, PlayUrlBean.class); //判断某个节目是否只跟一个账号相关联
                        if (sidlist != null && sidlist.size() == 1) {
                            //删除channel关联，根据streamid去Stream中查找节目信息，传入channelService删除
                            Query query = Query.query(Criteria.where("_id").is(streamid));
                            Stream stream = mongoTemplate.findOne(query, Stream.class);
                            streamService.deleteStreamRelation(stream.getStreamId(), stream.getCategoryId(), type);
                            mongoTemplate.remove(query, Stream.class);  //删除当前code和sn对应的stream详细信息
                        }
                    }
                }
                mongoTemplate.remove(Query.query(c), PlayUrlBean.class);  //删除当前code和sn对应的playurl
            }
            flag = "success";
        }catch (Exception e) {
            account.setStatus(-1);
            account.setErrorstr("request fail");
            e.printStackTrace();
        }
        account.setId(id);
        aAccountService.setStatusAndError(account);
        return flag;
    }

    private void getChannel(String channels, String categoryname, String userAgent, String code, String mac, String sn, int stype) {
        try {           
            JSONArray json = JSONArray.parseArray(channels); 
            List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
            String stream_id = "", category_id = "", stream_name = "", stream_icon = "", stream_url = "", type = "", iptv="sham";
            if (json.size() > 0) {
                for(int i=0; i<json.size(); i++){
                    JSONObject channel = json.getJSONObject(i);
                    if (0 == stype) {
                        stream_id = channel.getString("stream_id");
                        category_id = channel.getString("stream_pkg_id");
                        stream_name = channel.getString("stream_name");
                        stream_icon = channel.getString("stream_icon");
                        stream_url = channel.getString("stream_url");
                        type = "live";
                    }else if (1 == stype) {
                        stream_id = channel.getString("id");
                        category_id = channel.getString("category_id");
                        stream_name = channel.getString("stream_display_name");
                        stream_icon = channel.getString("stream_icon");
                        stream_url = channel.getString("stream_url");
                        type = "movie";
                    }
                    Criteria c = new Criteria();  
                    c.and("streamId").is(stream_id);
                    c.and("categoryId").is(category_id);
                    c.and("type").is(iptv);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class);
                    String mongo_id = "";
                    if(s != null){
                        mongo_id = s.get_id();
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(stream_id);
                        stream.setStreamName(stream_name);
                        stream.setStreamIcon(stream_icon);
                        stream.setStreamType(type); 
                        stream.setCategoryId(category_id);
                        stream.setCategoryname(categoryname);
                        stream.setType(iptv);
                        mongoTemplate.insert(stream); 
                        mongo_id = stream.get_id();
                    }
                    //存放播放链接
                    Criteria cp = new Criteria();  
                    cp.and("streamid").is(mongo_id);
                    cp.and("code").is(code);
                    cp.and("sn").is(sn);
                    cp.and("mac").is(mac);
                    cp.and("type").is(iptv);
                    PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                    if (pu != null) {
                        Update update = Update.update("playurl", stream_url).set("flag", 0);
                        mongoTemplate.updateFirst(Query.query(cp), update, PlayUrlBean.class);
                    }else {
                        PlayUrlBean playUrl = new PlayUrlBean();
                        playUrl.setStreamid(mongo_id);
                        playUrl.setPlayurl(stream_url);
                        playUrl.setCode(code);
                        playUrl.setMac(mac);
                        playUrl.setSn(sn);
                        playUrl.setType(iptv);
                        playUrl.setUserAgent(userAgent);
                        playUrl.setFlag(0);
                        list.add(playUrl);
                    }
                }
                if (list.size() != 0 && list.size() %10000 == 0){
                    mongoTemplate.insertAll(list);
                    list.clear();
                }
            }
            if (list.size() > 0) {
                mongoTemplate.insertAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
