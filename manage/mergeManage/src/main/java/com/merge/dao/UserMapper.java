package com.merge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.domain.UserBean;

@Mapper
public interface UserMapper {

    UserBean getUserByName(@Param("name")String name);

    UserBean getUser(@Param("name")String name, @Param("password")String password);

}
