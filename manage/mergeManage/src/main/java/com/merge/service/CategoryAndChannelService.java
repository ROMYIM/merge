package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.CategoryAndChannelMapper;
import com.merge.domain.CategoryAndChannelBean;

@Service
public class CategoryAndChannelService {

    @Resource
    private CategoryAndChannelMapper categoryAndChannelMapper;
    
    public Integer JudgeChannelExist(CategoryAndChannelBean c) {
        return categoryAndChannelMapper.JudgeChannelExist(c);
    }

    public void addChannel(List<CategoryAndChannelBean> list) {
        categoryAndChannelMapper.addChannel(list);
    }

    public int getCountCateAndChannel(int cid, String keyword, int status) {
        return categoryAndChannelMapper.getCountCateAndChannel(cid, keyword, status);
    }

    public List<CategoryAndChannelBean> getCateAndChannelList(Page<CategoryAndChannelBean> page) {
        return categoryAndChannelMapper.getCateAndChannelList(page);
    }

    public void delProgram(String channelids, int cid) {
        categoryAndChannelMapper.delProgram(channelids, cid);
    }

    public void delChannel(String ids) {
        categoryAndChannelMapper.delChannel(ids);
    }

    public void delByCategoryid(String categoryids) {
        categoryAndChannelMapper.delByCategoryid(categoryids);        
    }

    public List<CategoryAndChannelBean> getCategoryIdByChannels(String channelids) {
        return categoryAndChannelMapper.getCategoryIdByChannels(channelids);
    }

}
