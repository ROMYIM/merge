package com.merge.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.merge.config.HttpClientUtil;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.service.AgreementAccountService;
import com.merge.service.StreamService;

public class SamsatController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);
    
    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
            
    @Override
    public String baseLogin(int id, String code, String sn, MongoTemplate mongo) {
        String result = "", flag = "fail", type = "samsat";
        String url = "http://act.smi-iptv.com/stbact/actnew.php?code="+code+"&stbid="+sn;
        mongoTemplate = mongo;
        AgreementAccountBean account = new AgreementAccountBean();
        try {
            result = HttpClientUtil.sendHttpGet(url);
            String[] splits = result.split("\\|");  
            url = splits[0].replaceAll(" |link:", "");
            if(url.equals("") || url == null){
                flag = splits[1].replaceAll("msg:", "");
                account.setStatus(2);
                account.setErrorstr(flag);
                //当账号无效时，清除相关信息
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
                mongoTemplate.remove(Query.query(c), PlayUrlBean.class); //删除当前code和sn对应的playurl
            } else {  
                result = HttpClientUtil.sendHttpGet(url);
                JSONObject jsonobject = JSONObject.parseObject(result);
                JSONObject channels = JSONObject.parseObject(jsonobject.getString("available_channels")); //获取节目               

                //获取拼接播放链接相关信息
                JSONObject user_info = JSONObject.parseObject(jsonobject.getString("user_info"));
                JSONObject server_info = JSONObject.parseObject(jsonobject.getString("server_info"));
                String urlPrefix = "http://"+server_info.getString("url")+":"+server_info.getString("port")+"/";
                String urlMiddle = "/"+user_info.getString("username")+"/"+user_info.getString("password")+"/";
                getChannel(channels.toString(), urlPrefix, urlMiddle, code, sn); 
                account.setStatus(0);
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
     * @param urlMiddle 
     * @param urlPrefix 
     * @param sn 
     * @param code 
     * @date 2018年3月22日  上午9:43:23
     */
    private void getChannel(String channels, String urlPrefix, String urlMiddle, String code, String sn) {
        try {
            List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
            String mongo_id = "", extension = "", playurl = "", type = "samsat";
            LinkedHashMap<String, String> jsonMap = JSONObject.parseObject(channels, new TypeReference<LinkedHashMap<String, String>>(){});
            for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                JSONObject json = JSONObject.parseObject(entry.getValue());
                Criteria c = new Criteria();  
                c.and("streamId").is(json.getString("stream_id"));
                c.and("categoryId").is(json.getString("category_id"));
                c.and("type").is(type);
                Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                if(s != null){
                    mongo_id = s.get_id();
                    //continue;
                }else {
                    Stream stream = new Stream();
                    stream.setStreamId(json.getString("stream_id"));
                    stream.setStreamName(json.getString("name"));
                    stream.setStreamIcon(json.getString("stream_icon"));
                    stream.setStreamType(json.getString("stream_type"));
                    stream.setCategoryId(json.getString("category_id"));
                    stream.setCategoryname(json.getString("category_name"));
                    stream.setType(type);
                    mongoTemplate.insert(stream); 
                    mongo_id = stream.get_id();
                }
                if ("live".equals(json.getString("stream_type"))) {
                    extension = ".ts";
                }else if("movie".equals(json.getString("stream_type"))) {
                    extension = "."+json.getString("container_extension");
                }else {
                    extension = "";
                }
                playurl = urlPrefix + json.getString("stream_type") + urlMiddle + json.getString("stream_id") + extension;
                //存放播放链接
                Criteria cp = new Criteria();  
                cp.and("streamid").is(mongo_id);
                cp.and("playurl").is(playurl);
                PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                if (pu != null) {  //若已经存过，则不存
                    continue;
                }else {
                    PlayUrlBean playUrl = new PlayUrlBean();
                    playUrl.setStreamid(mongo_id);
                    playUrl.setPlayurl(playurl);
                    playUrl.setCode(code);
                    playUrl.setSn(sn);
                    playUrl.setType(type);
                    playUrl.setFlag(0);
                    list.add(playUrl);
                    //mongoTemplate.insert(playUrl);
                }
                if (list.size() != 0 && list.size() %10000 == 0){
                    mongoTemplate.insertAll(list);
                    list.clear();
                }
            }  
            if (list.size() > 0){
                mongoTemplate.insertAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
}
