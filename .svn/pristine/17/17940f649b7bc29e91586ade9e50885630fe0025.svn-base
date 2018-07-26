package com.merge.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.dao.UserMapper;
import com.merge.domain.UserBean;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public UserBean getUserByName(String name) {
        return userMapper.getUserByName(name);
    }

    public UserBean getUser(String name, String password) {
        return userMapper.getUser(name,password);
    }

    
    
}
