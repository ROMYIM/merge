package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.MergeUserMapper;
import com.merge.domain.MergeUserBean;

@Service
public class MergeUserService {

    @Resource
    private MergeUserMapper mergeUserMapper;

    public int getCountMergeUser(String keyword, int status) {
        return mergeUserMapper.getCountMergeUser(keyword, status);
    }

    public List<MergeUserBean> getMergeUserList(Page<MergeUserBean> page) {
        return mergeUserMapper.getMergeUserList(page);
    }

    public int editMergeUser(MergeUserBean user) {
        return mergeUserMapper.editMergeUser(user);
    }

    public int addMergeUser(MergeUserBean user) {
        return mergeUserMapper.addMergeUser(user);
    }

    public MergeUserBean getMergeUserById(int id) {
        return mergeUserMapper.getMergeUserById(id);
    }

    public void delMergeUserById(String ids) {
        mergeUserMapper.delMergeUserById(ids);
    }

    public int judgeMergeUserid(String userid) {
        return mergeUserMapper.judgeMergeUserid(userid);
    }

    
    
}
