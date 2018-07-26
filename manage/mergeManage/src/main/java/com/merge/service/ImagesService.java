package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.dao.ImagesMapper;
import com.merge.domain.ImagesBean;

@Service
public class ImagesService {
    
    @Resource
    private ImagesMapper imagesMapper;

    public void addImages(ImagesBean images) {
        imagesMapper.addImages(images);
    }
    
    public void editImages(ImagesBean images) {
        imagesMapper.editImages(images);
    }

    public ImagesBean getPtInfoById(int pid) {
        return imagesMapper.getPtInfoById(pid);
    }

    public List<ImagesBean> getPtByChannelid(int channelid) {
        return imagesMapper.getPtByChannelid(channelid);
    }

    public void delPtByIds(String ids) {
        imagesMapper.delPtByIds(ids);
    }

    public List<ImagesBean> getPtByIds(String ids) {
        return imagesMapper.getPtByIds(ids);
    }

    public void delPtByChannelids(String ids) {
        imagesMapper.delPtByChannelids(ids);
    }

    public List<ImagesBean> getPtByChannelids(String channelids, int addtype) {
        return imagesMapper.getPtByChannelids(channelids, addtype);
    }

}
