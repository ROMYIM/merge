package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.domain.CategoryBean;

@Mapper
public interface CategoryMapper {

    int addCategory(CategoryBean category);
    
    int editCategory(CategoryBean category);

    CategoryBean getCategoryById(@Param("categoryid")int categoryid);

    CategoryBean judgeByName(@Param("name")String name);

    List<CategoryBean> getCList();

    String[] getCategorysByParentId(@Param("parentid")String parentid);

    void delCategoryAndChildren(@Param("ids")String ids);

    String getCnameById(@Param("cid")int cid);

    void updateSnum(@Param("cid")int cid, @Param("snum")int snum);

    String[] getParentIdByCid(@Param("cid")String cid);

    List<CategoryBean> getTopCategory();

    int getTypeById(@Param("id")int id);

}
