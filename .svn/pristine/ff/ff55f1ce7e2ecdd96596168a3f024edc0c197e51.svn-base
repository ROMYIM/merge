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

public class BrothersTvController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RedisClient redisClient;
    
    @Override
    public String baseLogin(String code, String sn, RedisClient redis, MongoTemplate mongo) {
        String result = "",flag = "fail";
        String url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=info";
        redisClient = redis;
        mongoTemplate = mongo;
        try {
            result = HttpClientUtil.sendHttpGet(url);
            JSONObject jsonobject = JSONObject.parseObject(result);
            if (100 == Integer.parseInt(jsonobject.getString("status"))) {
                url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=CategoryList";
                result = HttpClientUtil.sendHttpGet(url);
                JSONObject jsonObj = JSONObject.parseObject(result);
                JSONArray jsonArray = jsonObj.getJSONObject("packages").getJSONArray("package");
                ArrayList<Object> categoryArray = new ArrayList<Object>();
                for(int i = 0; i < jsonArray.size(); i++) {
                    JSONObject category = jsonArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("category_name", category.get("name"));
                    map.put("category_id", category.get("id"));
                    categoryArray.add(map);
                }
                flag = "{\"category\":"+JSONObject.toJSON(categoryArray)+"}"; //返回分类列表信息
            }else {
                flag = jsonobject.get("message").toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    @Override
    public String getChannelById(String categoryid, String code, int offset, int limit, RedisClient redisClient, MongoTemplate mongoTemplate) {
        Integer start = offset * limit - limit + 1;
        Integer end = offset * limit;       
        List<Object> list = new ArrayList<Object>();
        String total = "0";
        Map<String, Object> result = new HashMap<String, Object>(); 
        try {
            if(!redisClient.exists(categoryid)){
                String url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=channelsList&category_id="+categoryid;
                String re = HttpClientUtil.sendHttpGet(url);
                JSONObject json = JSONObject.parseObject(re);
                JSONArray jsonArray = json.getJSONArray("tv_channel");
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject channel = jsonArray.getJSONObject(i);
                    //存入redis
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("stream_id", channel.get("id"));
                    map.put("stream_name", channel.get("caption"));
                    map.put("stream_icon", channel.get("icon_url"));
                    map.put("stream_url", channel.get("streaming_url"));
                    redisClient.lset(categoryid, channel.getString("id"));
                    redisClient.hset("s"+categoryid, channel.getString("id"), JSONObject.toJSONString(map));
                    //存入mongo
                    Criteria c = new Criteria();  
                    c.and("streamId").is(Integer.parseInt((String)channel.get("id")));
                    c.and("categoryId").is(categoryid);
                    c.and("type").is(2);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                    if(s != null){
                        continue;
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("id"));
                        stream.setStreamName(channel.getString("caption"));
                        stream.setStreamIcon(channel.getString("icon_url"));
                        stream.setStreamType("");
                        stream.setCategoryId(categoryid);
                        stream.setType(2);
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
