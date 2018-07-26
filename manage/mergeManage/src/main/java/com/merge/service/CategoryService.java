package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.dao.CategoryMapper;
import com.merge.domain.CategoryBean;

@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    public int addCategory(CategoryBean category) {
        return categoryMapper.addCategory(category);
    }

    public int editCategory(CategoryBean category) {
        return categoryMapper.editCategory(category);
    }

    public CategoryBean getCategoryById(int categoryid) {
        return categoryMapper.getCategoryById(categoryid);
    }

    public CategoryBean judgeByName(String name) {
        return categoryMapper.judgeByName(name);
    }

    public List<CategoryBean> getCList() {
        return categoryMapper.getCList();
    }

    public String[] getCategorysByParentId(String parentId) {
        return categoryMapper.getCategorysByParentId(parentId);
    }

    public void delCategoryAndChildren(String ids) {
        categoryMapper.delCategoryAndChildren(ids);
    }

    public String getCnameById(int cid) {
        return categoryMapper.getCnameById(cid);
    }

    public void updateSnum(int cid, int snum) {
        categoryMapper.updateSnum(cid, snum);
    }

    public String[] getParentIdByCid(String cid) {
        return categoryMapper.getParentIdByCid(cid);
    }

    public List<CategoryBean> getTopCategory() {
        return categoryMapper.getTopCategory();
    }

    public int getTypeById(int id) {
        return categoryMapper.getTypeById(id);
    }

}
