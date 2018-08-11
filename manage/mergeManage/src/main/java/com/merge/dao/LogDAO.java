package com.merge.dao;

import javax.annotation.Resource;

import com.merge.domain.LogBean;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * LogDAO
 */
@Component
public class LogDAO {

    @Resource
    private MongoTemplate mongoTemplate;

    public void insertLog(LogBean logBean) {
        mongoTemplate.insert(logBean, "log");
    }
}