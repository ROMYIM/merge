package com.zuul.utils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisUtils {
	@Autowired
	RedisTemplate<String, String> redisTemplate;
    
    @Resource(name = "redisTemplate")
	ValueOperations<String, String> stringOps;
    
    @Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOps;
    
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}
	
	public String get(String key) {
		return stringOps.get(key);
	}
	public String hashKey(String key, String field) {
		return hashOps.get(key, field);
	}
		
	public void set(String key, String value) {
		stringOps.set(key, value);
	}
		
	public void set(String key, String value, int expireTime) {
		stringOps.set(key, value, expireTime, TimeUnit.SECONDS);
	}
	
	public void delete(String key) {
		redisTemplate.delete(key);
	}
	
	public Set<String> keys(String key){
		return redisTemplate.keys(key);
	}
	
	public void deleteSet(Set<String> set) {
		redisTemplate.delete(set);
	}
}
