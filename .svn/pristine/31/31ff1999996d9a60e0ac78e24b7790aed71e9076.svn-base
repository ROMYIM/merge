package com.merge.util;

import java.util.ArrayList;
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

public class FerarriController extends BaseLogin {
    
    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);
    
    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
    
    @Override
    public String baseLogin(int id, String code, String sn, String mac, MongoTemplate mongo) {
        String flag = "fail", result = "", type = "ferarri";
        mongoTemplate = mongo;
        AgreementAccountBean account = new AgreementAccountBean();
        try {
            String url = "https://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=active&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
            result = HttpClientUtil.sendHttpGet(url);
            String[] arr = result.split("Decoded data = ");
            JSONObject json = JSONObject.parseObject(arr[1].trim());
            if (json.getString("status").equals("0") || json.getString("status").equals("1")) {
                //========= live 可直接播放===============================
                //packages
                url = "http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=packages&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                String[] packagestr = result.split("Decoded data = ");
                //channels
                url = "http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=channels&code="+code+"&mac="+mac+"&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                String[] channelstr = result.split("Decoded data = ");
                getChannel(channelstr[1].trim(), packagestr[1].trim(), code, mac, sn, 0);
                
                //================ movie 可直接播放====================
                url = "http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=movies_cat&code="+code+"&mac="+mac+"&sn="+sn+"&parent=all&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                List<String> parent = new ArrayList<>();
                JSONArray categoryArr = new JSONArray();
                String[] categorystr = result.split("Decoded data = ");
                categoryArr = JSONArray.parseArray(categorystr[1].trim());
                if (categoryArr.size() > 0) {
                    for (int i = 0; i < categoryArr.size(); i++) {
                        if (parent.contains(categoryArr.getJSONObject(i).getString("parent_id"))) {
                            continue;
                        }else {
                            parent.add(categoryArr.getJSONObject(i).getString("parent_id"));
                        }
                    }
                    //由于分类下若有子分类，节目会为空，故先剔除有子分类的顶级分类
                    if (parent.size() > 0) {
                        for (int m = 0; m < categoryArr.size(); m++) {
                            for (int n = 0; n < parent.size(); n++) {
                                if (categoryArr.getJSONObject(m).getString("id").equals(parent.get(n))) {
                                    categoryArr.remove(m);
                                }
                            }
                        }
                    }
                }
                //Channel
                url = "http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=movies_list&code="+code+"&mac="+mac+"&sn="+sn+"&catid=all&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                String[] channels = result.split("Decoded data = ");
                getChannel(channels[1].trim(), categoryArr.toString(), code, mac, sn, 1);
                account.setStatus(0);
            } else {
                if (json.getString("status").equals("3")) {
                    account.setStatus(1);
                }else if (json.getString("status").equals("2") || json.getString("status").equals("4")) {
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
                        List<PlayUrlBean> sidlist = mongoTemplate.find(q, PlayUrlBean.class);
                        if (sidlist != null && sidlist.size() == 1) {
                            //删除channel关联，根据streamid去Stream中查找节目信息，传入channelService删除
                            Query query = Query.query(Criteria.where("_id").is(streamid));
                            Stream stream = mongoTemplate.findOne(query, Stream.class);
                            streamService.deleteStreamRelation(stream.getStreamId(), stream.getCategoryId(), type);
                            mongoTemplate.remove(query, Stream.class); //删除当前code和sn对应的stream详细信息
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
    
    private void getChannel(String channels, String categorys, String code, 
            String mac, String sn, int stype){
        List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
        JSONArray channelArr = JSONArray.parseArray(channels);
        JSONArray categoryArr = JSONArray.parseArray(categorys);
        String categoryname = "", stream_id="", category_id="", stream_name="", stream_icon="", stream_url="", type="", iptv="ferarri";
        if (channelArr.size() > 0) {
            if (stype == 0) {
                type = "live";
            }else if (stype == 1) {
                type = "movie";
            }
            for (int i=0; i<channelArr.size(); i++) {
                JSONObject channel = channelArr.getJSONObject(i);
                for (int j=0; j<categoryArr.size(); j++) {
                    JSONObject category = categoryArr.getJSONObject(j);
                    if (channel.getString("category_id").equals(category.getString("id"))) {
                        categoryname = category.getString("category_name");
                    }
                }
                stream_id = channel.getString("id");
                category_id = channel.getString("category_id");
                stream_name = channel.getString("stream_display_name");
                stream_icon = channel.getString("stream_icon");
                stream_url = channel.getString("stream_url");
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
                    playUrl.setFlag(0);
                    list.add(playUrl);
                }
                if (list.size() != 0 && list.size() %10000 == 0){
                    mongoTemplate.insertAll(list);
                    list.clear();
                }
            }
            if (list.size() > 0) {
                mongoTemplate.insertAll(list);
            }
        }
    }

}
