package com.merge.util;

import org.springframework.data.mongodb.core.MongoTemplate;

public class BaseLogin {
    
    public String baseLogin(int id, String code, String sn, MongoTemplate mongoTemplate) {
        return null;
    }
    
    public String baseLogin(int id, String code, String sn, int streamType, MongoTemplate mongoTemplate) {
        return null;
    }
    
    
    public String getChannelById(String categoryid, String code, MongoTemplate mongoTemplate){
        return null;        
    }
    
    public String getChannelById(String categoryid, int streamType, MongoTemplate mongoTemplate){
        return null;        
    }
    
    public String baseLogin(int id, String code, String sn, String mac, MongoTemplate mongoTemplate) {
        return null;
    }
}
