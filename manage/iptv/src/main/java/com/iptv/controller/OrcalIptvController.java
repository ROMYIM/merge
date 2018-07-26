package com.iptv.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iptv.config.BaseLogin;
import com.iptv.config.HttpClientUtil;
import com.iptv.config.RedisClient;
import com.iptv.domain.Stream;

public class OrcalIptvController extends BaseLogin {
    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RedisClient redisClient;
    
    @Override
    public String baseLogin(String code, String sn, RedisClient redis, MongoTemplate mongo) {       
        String url = "https://www.orcaapi.com/api/v1.1", flag = "fail";
        redisClient = redis;
        mongoTemplate = mongo;
        try {
            JSONObject json = firstLogin(url+"/startrack/checkActiveCode?", code, sn);
            if (1 == json.getInteger("result")) {
                String token = json.getString("access_token");
                flag = getLiveTvData("https://www.orcaapi.com/api/v1.1/liveTv", token);
            } else {
                JSONObject jsonResult = secondLogin(url + "/startrack/checkMacAddr?", sn);
                if (1 == jsonResult.getInteger("result")) {
                    String token = jsonResult.getString("access_token");
                    flag = getLiveTvData("https://www.orcaapi.com/api/v1.1/liveTv", token);
                } else {
                    flag = json.getString("msg"); //code invalid,获取返回的message
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    
    /**
     * 
     * @Method: firstLogin
     * @Author: clear
     * @Date: 2018年1月16日 下午3:42:18
     * @Description: orca iptv first login
     */
    public JSONObject firstLogin(String url, String activeCode, String macaddress) {
        // json参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activeCode", activeCode);
        jsonObject.put("macaddress", macaddress);
        String strJson = jsonObject.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, strJson, "utf-8","");
            JSONObject json = JSONObject.parseObject(result);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @Method: secondLogin
     * @Author: clear
     * @Date: 2018年1月16日 下午3:56:11
     * @Description: orca iptv second login
     */
    public JSONObject secondLogin(String url, String macaddress) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("macaddress", macaddress);
        String strJson = jsonObject.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, strJson, "utf-8","");
            JSONObject json = JSONObject.parseObject(result);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @MethodName:getLiveTvData
     * @Description:获取节目和分类信息
     * @author Windy
     * @date 2018年1月24日  上午10:45:17
     */
    public String getLiveTvData(String url, String token) {
        String Authorization = "Bearer " + token;
        try {
            String result = HttpClientUtil.doHttpsPost(url, "", "utf-8", Authorization);
            JSONObject json = JSONObject.parseObject(result);
            JSONArray jsonArray = json.getJSONArray("data");
            ArrayList<Object> categoryArray = new ArrayList<Object>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonBean = jsonArray.getJSONObject(i); 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("category_name", jsonBean.get("group_name"));
                map.put("category_id", jsonBean.get("group_id"));
                categoryArray.add(map);
                JSONArray channelArray = jsonBean.getJSONArray("channels");
                for(int j=0;j<channelArray.size();j++){
                    JSONObject channel = channelArray.getJSONObject(j);                   
                    //存入redis
                    Map<String, Object> channelmap = new HashMap<String, Object>();
                    channelmap.put("stream_id", channel.get("channel_id"));
                    channelmap.put("stream_name", channel.get("channel_name"));
                    channelmap.put("stream_icon", channel.get("channel_image"));
                    channelmap.put("stream_url", channel.get("channel_url"));
                    redisClient.lset(jsonBean.get("group_id").toString(), channel.getString("channel_id"));
                    redisClient.hset("s"+jsonBean.get("group_id"), channel.getString("channel_id"), JSONObject.toJSONString(channelmap));
                    
                    //存入mongo
                    Criteria c = new Criteria(); 
                    c.and("streamId").is(channel.getShort("channel_id"));
                    c.and("categoryId").is(jsonBean.get("group_id"));
                    c.and("type").is(3);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class);
                    if(s != null){
                        continue;
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("channel_id"));
                        stream.setStreamName(channel.getString("channel_name"));
                        stream.setStreamIcon(channel.getString("channel_image"));
                        stream.setStreamType("live");
                        stream.setCategoryId(jsonBean.getString("group_id"));
                        stream.setType(3);
                        mongoTemplate.save(stream); 
                    }
                }
            }
            String category = "{\"category\":"+JSONObject.toJSON(categoryArray)+"}";
            return category;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

    @Override
    public String getChannelById(String categoryid, int offset, int limit, RedisClient redisClient) {
        Integer start = offset * limit - limit + 1;
        Integer end = offset * limit;       
        List<Object> list = new ArrayList<Object>();
        String total = "0";
        Map<String, Object> result = new HashMap<String, Object>(); 
        try {
            List<String> slist = redisClient.lrange(categoryid, start, end);
            for(String streamid : slist) {
                list.add(JSONObject.parseObject(redisClient.hget("s"+categoryid, streamid)));
            }
            total = (redisClient.llen(categoryid)).toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(list==null||list.size()==0){
            result.put("rows", "");
        }else{
            result.put("rows", list);
        }
        result.put("total", total);
        return JSONObject.toJSONString(result).toString();
    }
    
    @Override
    public String baseLogin(String code, String sn, int streamType, RedisClient redisClient, MongoTemplate mongoTemplate) {
        String url = "https://www.orcaapi.com/api/v1.1", flag = "fail", result = "", language = "EN";
        try {
            if (2 == streamType) {
                language = "FR";
            }else if (3 == streamType) {
                language = "AR";
            }
            JSONObject json = firstLogin(url+"/startrack/checkActiveCode?", code, sn);
            if (1 == json.getInteger("result")) {
                String token = json.getString("access_token");
                redisClient.set("token", token);
                url = "https://www.orcaapi.com/api/v1.1/ss/vod/language/"+language;
                result = HttpClientUtil.doHttpsGet(url, "utf-8", "");
            } else {
                JSONObject jsonResult = secondLogin(url + "/startrack/checkMacAddr?", sn);
                if (1 == jsonResult.getInteger("result")) {
                    String token = jsonResult.getString("access_token");
                    redisClient.set("token", token);
                    url = "https://www.orcaapi.com/api/v1.1/ss/vod/language/"+language;
                    result = HttpClientUtil.doHttpsGet(url, "utf-8", "");
                    flag = "{\"category\":"+result+"}";
                } else {
                    flag = json.getString("msg"); //code invalid,获取返回的message
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    @Override
    public String getChannelById(String categoryid, int streamType, int offset, int limit, RedisClient redisClient, MongoTemplate mongoTemplate){
        Integer start = offset * limit - limit + 1;
        Integer end = offset * limit;       
        List<Object> list = new ArrayList<Object>();
        String total = "0", language = "EN";
        Map<String, Object> result = new HashMap<String, Object>(); 
        if (2 == streamType) {
            language = "FR";
        }else if (3 == streamType) {
            language = "AR";
        }
        try {
            if(!redisClient.exists(categoryid)){
                String url = "https://www.orcaapi.com/api/v1.1/ss/vod/language/"+language+"/category/"+categoryid;
                String re = HttpClientUtil.doHttpsGet(url, "utf-8", "");
                JSONArray jsonArray = JSONArray.parseArray(re);
                String Authorization = "Bearer " + redisClient.get("token"), playUrl = "";              
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject channel = jsonArray.getJSONObject(i);
                    //获取播放链接
                    url = "https://www.orcaapi.com/api/v1.1/ss/vod/movie/"+channel.get("imdb_id");
                    playUrl = HttpClientUtil.doHttpsGet(url, "utf-8", Authorization);
                    JSONObject urlresult = JSONObject.parseObject(playUrl);
                    //存入redis
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("stream_id", channel.get("imdb_id"));
                    map.put("stream_name", channel.get("name"));
                    map.put("stream_icon", channel.get("image"));
                    map.put("stream_url", urlresult.getString("url"));
                    redisClient.lset(categoryid, channel.getString("imdb_id"));
                    redisClient.hset("s"+categoryid, channel.getString("imdb_id"), JSONObject.toJSONString(map));
                    //存入mongo
                    Criteria c = new Criteria();  
                    c.and("streamId").is(channel.get("imdb_id"));
                    c.and("categoryId").is(categoryid);
                    c.and("type").is(3);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                    if(s != null){
                        continue;
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("imdb_id"));
                        stream.setStreamName(channel.getString("name"));
                        stream.setStreamIcon(channel.getString("image"));
                        stream.setStreamType("movie");
                        stream.setCategoryId(categoryid);  //类型，需要修改 
                        stream.setType(3);
                        mongoTemplate.save(stream); 
                    }
                }
            }
            List<String> slist = redisClient.lrange(categoryid, start, end);
            for(String streamid : slist) {
                list.add(JSONObject.parseObject(redisClient.hget("s"+categoryid, streamid)));
            }
            total = (redisClient.llen(categoryid)).toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(list==null||list.size()==0){
            result.put("rows", "");
        }else{
            result.put("rows", list);
        }
        result.put("total", total);
        return JSONObject.toJSONString(result).toString();             
    }
    
}
