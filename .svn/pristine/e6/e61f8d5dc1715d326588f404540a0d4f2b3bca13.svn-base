package com.merge.util;

import java.util.LinkedList;
//import java.util.LinkedList;
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

public class MsController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);
    
    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
    
    @Override
    public String baseLogin(int id, String code, String sn, MongoTemplate mongo) {
        String result = "", flag = "fail", type = "Ms";
        String url = "http://m-iptv.net:443/IPTV/API-V4.php?mode=active&code="+code+"&mac=&sn="+sn+"&raw=yes";
        mongoTemplate = mongo;
        AgreementAccountBean account = new AgreementAccountBean();
        try{
            result = HttpClientUtil.sendHttpGet(url); 
            String[] array = result.split("data=|data =");         
            JSONObject jsonobject = JSONObject.parseObject(array[1].trim());
            if (jsonobject.getString("status").toString().equals("100")) { 
                String user_agent = jsonobject.getString("user_agent");  //用于播放
                url = "http://m-iptv.net:443/IPTV/API-V4.php?mode=packages&code="+code+"&mac=&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                String[] narray = result.split("data=|data =");         
                JSONArray json = JSONArray.parseArray(narray[1].trim());
                for(int i=0;i<json.size();i++){
                    JSONObject category = json.getJSONObject(i);
                    getChannel(category.getString("channels"), category.getString("category_name"), user_agent, code, sn);
                }
                account.setStatus(0);
            }else {
                String status = jsonobject.getString("status").toString();
                if (status.equals("102") || status.equals("104")) {
                    account.setStatus(2);
                }else if (status.equals("103")) {
                    account.setStatus(1);
                }else if(status.equals("115")) {
                    account.setStatus(1);
                }
                account.setErrorstr(jsonobject.get("message").toString());
                //当账号无效时，清除playurl
                Criteria c = new Criteria();  
                c.and("code").is(code);
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
    
    /**
     * 
     * @MethodName:getChannel
     * @Description:根据频道处理节目单存入mongo
     * @author Windy
     * @param sn 
     * @param code 
     * @param sn2 
     * @param sn2 
     * @date 2018年3月22日  上午9:50:07
     */
    private void getChannel(String channels, String categoryname, String userAgent, String code, String sn) {
        try {           
            String type = "Ms";
            JSONArray json = JSONArray.parseArray(channels); 
            List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
            if (json.size() > 0){
                for(int i=0; i<json.size(); i++){
                    JSONObject channel = json.getJSONObject(i);
                    if (!channel.getString("stream_url").equals("empty")) {
                        Criteria c = new Criteria();  
                        c.and("streamId").is(channel.getString("stream_id"));
                        c.and("categoryId").is(channel.getString("category_id"));
                        c.and("type").is(type);
                        Stream s = mongoTemplate.findOne(Query.query(c), Stream.class);
                        String mongo_id = "";
                        if(s != null){
                            mongo_id = s.get_id();
                        }else {
                            Stream stream = new Stream();
                            stream.setStreamId(channel.getString("stream_id"));
                            stream.setStreamName(channel.getString("stream_name"));
                            stream.setStreamIcon(channel.getString("stream_icon"));
                            stream.setStreamType("none");  //暂时设为这个
                            stream.setCategoryId(channel.getString("category_id"));
                            stream.setCategoryname(categoryname);
                            stream.setType(type);
                            mongoTemplate.insert(stream); 
                            mongo_id = stream.get_id();  //获取刚插入的数据的id
                        }
                        //存放播放链接
                        Criteria cp = new Criteria();  
                        cp.and("streamid").is(mongo_id);
                        cp.and("code").is(code);
                        cp.and("sn").is(sn);
                        cp.and("type").is(type);
                        //cp.and("playurl").is(channel.getString("stream_url")); //Ms每次请求，用户名不变，密码会变
                        PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                        if (pu != null) {
                            Update update = Update.update("playurl", channel.getString("stream_url")).set("flag", 0);
                            mongoTemplate.updateFirst(Query.query(cp), update, PlayUrlBean.class);  //批量更新，后边再考虑
                        }else {
                            PlayUrlBean playUrl = new PlayUrlBean();
                            playUrl.setStreamid(mongo_id);
                            playUrl.setPlayurl(channel.getString("stream_url"));
                            playUrl.setCode(code);
                            playUrl.setSn(sn);
                            playUrl.setType(type);
                            playUrl.setUserAgent(userAgent);
                            playUrl.setFlag(0);
                            list.add(playUrl);
                            //mongoTemplate.insert(playUrl);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    
}
