package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.StreamMapper;
import com.merge.domain.StreamBean;

@Service
public class StreamService {

    @Resource
    private StreamMapper streamMapper;
    
    public int getCountStream(int channelid, String keyword) {
        return streamMapper.getCountStream(channelid, keyword);
    }

    public List<StreamBean> getStreamList(Page<StreamBean> page) {
        return streamMapper.getStreamList(page);
    }

    public void addStream(List<StreamBean> list) {
        streamMapper.addStream(list);
    }

    public void delStream(String sids) {
        streamMapper.delStream(sids);
    }

    public Integer getStream(StreamBean stream) {
        return streamMapper.getStream(stream);
    }

    public void delStreamByChannelids(String ids) {
        streamMapper.delStreamByChannelids(ids);
    }

    public void deleteStreamRelation(String streamid, String categoryid, String type) {
        //删除stream与channel的关联
        streamMapper.deleteStreamRelation(streamid, categoryid, type);
    }

}
