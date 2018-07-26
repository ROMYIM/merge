package com.iptv.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.iptv.config.BaseLogin;
import com.iptv.config.HttpClientUtil;
import com.iptv.config.RedisClient;
import com.iptv.domain.Stream;

public class SamsatController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RedisClient redisClient;
    
    @Override
    public String baseLogin(String code, String sn, RedisClient redis,MongoTemplate mongo) {
        String result = "", flag = "fail";
        String url = "http://act.smi-iptv.com/stbact/actnew.php?code="+code+"&stbid="+sn;
        redisClient = redis;
        mongoTemplate = mongo;
        try {
            redisClient.flushdb();
            result = HttpClientUtil.sendHttpGet(url);
            String[] splits = result.split("\\|");  
            url = splits[0].replaceAll(" |link:", "");
            if(url.equals("") || url == null){
                flag = splits[1].replaceAll("msg:", "");
            } else {  
                result = HttpClientUtil.sendHttpGet(url);
                JSONObject jsonobject = JSONObject.parseObject(result);
                JSONObject categorytype = JSONObject.parseObject(jsonobject.getString("categories"));  //获取分类
                flag = "{\"category\":"+categorytype.getString("live").substring(0,categorytype.getString("live").length()-1)+","+categorytype.getString("movie").substring(1,categorytype.getString("movie").length())+"}";
                JSONObject channels = JSONObject.parseObject(jsonobject.getString("available_channels")); //获取节目
                //获取链接相关信息
                Map<String, Object> map = new HashMap<String, Object>();
                JSONObject user = JSONObject.parseObject(jsonobject.getString("user_info"));
                JSONObject server = JSONObject.parseObject(jsonobject.getString("server_info"));
                map.put("username", user.getString("username"));
                map.put("password", user.getString("password"));
                map.put("url", server.getString("url")+":"+server.getString("port"));
                getChannel(channels.toString(), JSONObject.toJSONString(map)); 
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
     * @date 2018年3月22日  上午9:43:23
     */
    private void getChannel(String channels, String extents) {
        try {
            JSONObject ext = JSONObject.parseObject(extents);
            String username = ext.getString("username");
            String password = ext.getString("password");
            String url = ext.getString("url");
            LinkedHashMap<String, String> jsonMap = JSONObject.parseObject(channels, new TypeReference<LinkedHashMap<String, String>>(){});
            for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                JSONObject json = JSONObject.parseObject(entry.getValue());
                Map<String, Object> map = new HashMap<String, Object>();
                String stream_url = "";
                map.put("stream_id", json.get("stream_id"));
                map.put("stream_name", json.get("name"));
                map.put("stream_icon", json.get("stream_icon"));
                if (json.get("stream_type").equals("live")) {
                    stream_url = "http://"+url+"/"+json.get("stream_type")+"/"+username+"/"+password+"/"+json.get("stream_id")+".ts";
                }else if (json.get("stream_type").equals("movie")) {
                    stream_url = "http://"+url+"/"+json.get("stream_type")+"/"+username+"/"+password+"/"+json.get("stream_id")+"."+json.get("container_extension");
                }
                map.put("stream_url", stream_url);
                redisClient.lset(json.getString("category_id"), json.getString("stream_id"));
                redisClient.hset("s"+json.getString("category_id"), json.getString("stream_id"), JSONObject.toJSONString(map));            
                //存入mongo
                Criteria c = new Criteria();  
                c.and("streamId").is(Integer.parseInt(json.getString("stream_id")));
                c.and("categoryId").is(Integer.parseInt(json.getString("category_id")));
                c.and("type").is(0);
                Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                if(s != null){
                    continue;
                }else {
                    Stream stream = new Stream();
                    stream.setStreamId(json.getString("stream_id"));
                    stream.setStreamName(json.getString("name"));
                    stream.setStreamIcon(json.getString("stream_icon"));
                    stream.setStreamType(json.getString("stream_type"));
                    stream.setCategoryId(json.getString("category_id"));
                    stream.setType(0);
                    mongoTemplate.save(stream); 
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
