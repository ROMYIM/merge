package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.MergeUserBean;

@Mapper
public interface MergeUserMapper {

    int getCountMergeUser(@Param("keyword")String keyword, @Param("status")int status);

    List<MergeUserBean> getMergeUserList(Page<MergeUserBean> page);

    int editMergeUser(MergeUserBean user);

    int addMergeUser(MergeUserBean user);

    MergeUserBean getMergeUserById(@Param("id")int id);

    void delMergeUserById(@Param("ids")String ids);

    int judgeMergeUserid(@Param("userid")String userid);

}
