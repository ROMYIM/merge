package com.iptv.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisClient {

    @Autowired  
    private JedisPool jedisPool;  
      
    /**
     * 
     * @MethodName:hset
     * @Description:将哈希表 key中的字段 field的值设为 value
     * @author Windy
     * @date 2017年12月7日  上午10:44:02
     */
    public void hset(String key, String field, String value) throws Exception{
        Jedis jedis = null;  
        try {  
            jedis = jedisPool.getResource();  
            jedis.hset(key, field, value);
        } finally {  
            jedis.close();  
        }  
    }
    
    /**
     * 
     * @MethodName:hget
     * @Description:获取存储在哈希表中指定字段的值
     * @author Windy
     * @date 2017年12月7日  上午10:44:37
     */
    public String hget(String key, String field) throws Exception  {          
        Jedis jedis = null;  
        try {  
            jedis = jedisPool.getResource();  
            return jedis.hget(key, field);  
        } finally {  
            jedis.close();  
        }  
    }  
    
    /**
     * 
     * @MethodName:lset
     * @Description:将一个或多个值插入到列表头部
     * @author Windy
     * @date 2017年12月7日  上午10:47:33
     */
    public void lset(String key, String value) throws Exception{
        Jedis jedis = null;  
        try {  
            jedis = jedisPool.getResource();  
            jedis.lpush(key, value);
        } finally {  
            jedis.close();  
        }  
    }   
    
    /**
     * 
     * @MethodName:lrange
     * @Description:获取列表指定范围内的元素
     * @author Windy
     * @date 2017年12月7日  上午10:50:37
     */
    public List<String> lrange(String key, int start, int end) throws Exception  {          
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            return jedis.lrange(key, start, end);  
        } finally {  
            jedis.close();  
        }  
    }
    
    /**
     * 
     * @MethodName:exists
     * @Description:判断某个key是否存在
     * @author Windy
     * @date 2018年1月3日  上午11:33:21
     */
    public Boolean exists (String key) {
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            return jedis.exists(key);  
        } finally {  
            jedis.close();  
        } 
    }
    
    /**
     * 
     * @MethodName:flushdb
     * @Description:清除当前数据库缓存
     * @author Windy
     * @date 2018年1月3日  下午2:33:08
     */
    public String flushdb () {
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            return jedis.flushDB();  
        } finally {
            jedis.close();  
        } 
    }
    
    /**
     * 
     * @MethodName:llen
     * @Description:计算list长度
     * @author Windy
     * @date 2018年1月3日  下午2:34:10
     */
    public Long llen(String key) {
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            return jedis.llen(key);  
        } finally {
            jedis.close();  
        } 
    }

    /**
     * 
     * @MethodName:set
     * @Description:设置key(String)
     * @author Windy
     * @date 2018年3月23日  上午10:11:34
     */
    public void set(String key, String token) {
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            jedis.set(key, token);  
        } finally {
            jedis.close();  
        } 
        
    }

    /**
     * 
     * @MethodName:get
     * @Description:获取key的值（string）
     * @author Windy
     * @date 2018年3月23日  上午10:12:02
     */
    public String get(String key) {
        Jedis jedis = null; 
        try {  
            jedis = jedisPool.getResource();  
            return jedis.get(key);  
        } finally {
            jedis.close();  
        } 
    }
}
