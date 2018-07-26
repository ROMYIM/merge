package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.MergeGroupMapper;
import com.merge.domain.MergeGroupBean;

@Service
public class MergeGroupService {

    @Resource
    private MergeGroupMapper mergeGroupMapper;
    
    public int getCountMergeGroup(String keyword) {
        return mergeGroupMapper.getCountMergeGroup(keyword);
    }

    public List<MergeGroupBean> getMergeGroupList(Page<MergeGroupBean> page) {
        return mergeGroupMapper.getMergeGroupList(page);
    }

    public int editMergeGroup(MergeGroupBean group) {
        return mergeGroupMapper.editMergeGroup(group);
    }

    public int addMergeGroup(MergeGroupBean group) {
        return mergeGroupMapper.addMergeGroup(group);
    }

    public MergeGroupBean getMergeGroupById(int id) {
        return mergeGroupMapper.getMergeGroupById(id);
    }

    public void delMergeGroup(String ids) {
        mergeGroupMapper.delMergeGroup(ids);
    }

    public int judgeMergeGroup(String name) {
        return mergeGroupMapper.judgeMergeGroup(name);
    }

    public List<MergeGroupBean> getAllMergeGroup() {
        return mergeGroupMapper.getAllMergeGroup();
    }

}
