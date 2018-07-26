package com.zuul.service;

import org.springframework.stereotype.Service;

import com.zuul.utils.RedisUtils;


@Service
public class ZuulService extends RedisUtils{
	
	public String getKey(String key) {
		return get(key);
	}
	
	public String getHashKey(String key, String field) {
		return hashKey(key, field);
	}
	
}
