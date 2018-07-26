package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.MergeGroupBean;

@Mapper
public interface MergeGroupMapper {

    int getCountMergeGroup(@Param("keyword")String keyword);

    List<MergeGroupBean> getMergeGroupList(Page<MergeGroupBean> page);

    int editMergeGroup(MergeGroupBean group);

    int addMergeGroup(MergeGroupBean group);

    MergeGroupBean getMergeGroupById(@Param("id")int id);

    void delMergeGroup(@Param("ids")String ids);

    int judgeMergeGroup(@Param("name")String name);

    List<MergeGroupBean> getAllMergeGroup();

}
