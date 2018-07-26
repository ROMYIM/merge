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

public class MsController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RedisClient redisClient;
    
    @Override
    public String baseLogin(String code, String sn, RedisClient redis, MongoTemplate mongo) {
        String result = "", flag = "fail";
        String url = "http://m-iptv.net:443/IPTV/API-V4.php?mode=active&code="+code+"&mac=&sn="+sn+"&raw=yes";
        redisClient = redis;
        mongoTemplate = mongo;
        try{
            result = HttpClientUtil.sendHttpGet(url); 
            String[] array = result.split("data=|data =");         
            JSONObject jsonobject = JSONObject.parseObject(array[1].trim());
            if (jsonobject.getString("status").toString().equals("100")) {
                JSONObject info = JSONObject.parseObject(array[1].trim());
                redisClient.hset("playUrl", "user_agent", info.getString("user_agent"));
    
                url = "http://m-iptv.net:443/IPTV/API-V4.php?mode=packages&code="+code+"&mac=&sn="+sn+"&raw=yes";
                result = HttpClientUtil.sendHttpGet(url);
                String[] narray = result.split("data=|data =");         
                JSONArray json = JSONArray.parseArray(narray[1].trim());
                ArrayList<Object> categoryArray = new ArrayList<Object>();
                String channelStr = "[";
                for(int i=0;i<json.size();i++){
                    JSONObject category = json.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("category_name", category.get("category_name"));
                    map.put("category_id", category.get("id"));
                    categoryArray.add(map);
                    channelStr += category.get("channels").toString().substring(1,category.getString("channels").length()-1)+",";
                }
                flag = "{\"category\":"+JSONObject.toJSON(categoryArray)+"}";  //返回分类列表信息
                channelStr =  channelStr.substring(0, channelStr.length()-1) +"]"; //获取节目信息
                getChannel(channelStr); 
            }else {
               flag = jsonobject.get("message").toString();
            }    
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    /**
     * 
     * @MethodName:getChannel
     * @Description:根据频道处理节目单存入redis和mongo
     * @author Windy
     * @date 2018年3月22日  上午9:50:07
     */
    private void getChannel(String channels) {
        try {           
            JSONArray json = JSONArray.parseArray(channels); 
            if(json.size()>0){
                for(int i=0;i<json.size();i++){
                    JSONObject channel = json.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("stream_id", channel.get("stream_id"));
                    map.put("stream_name", channel.get("stream_name"));
                    map.put("stream_icon", channel.get("stream_icon"));
                    map.put("stream_url", channel.get("stream_url"));
                    redisClient.lset(channel.getString("category_id"), channel.getString("stream_id"));
                    redisClient.hset("s"+channel.getString("category_id"), channel.getString("stream_id"), JSONObject.toJSONString(map));            
                    //存入mongo
                    Criteria c = new Criteria();  
                    c.and("streamId").is(Integer.parseInt(channel.getString("stream_id")));
                    c.and("categoryId").is(Integer.parseInt(channel.getString("category_id")));
                    c.and("type").is(1);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                    if(s != null){
                        continue;
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("stream_id"));
                        stream.setStreamName(channel.getString("stream_name"));
                        stream.setStreamIcon(channel.getString("stream_icon"));
                        stream.setStreamType("");
                        stream.setCategoryId(channel.getString("category_id"));
                        stream.setType(1);
                        mongoTemplate.save(stream); 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
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
//            if (redisClient.exists("playUrl")) {
//                result.put("user_agent", redisClient.hget("playUrl", "user_agent"));
//            }
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
