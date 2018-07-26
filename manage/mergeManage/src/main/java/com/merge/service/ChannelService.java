package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.ChannelMapper;
import com.merge.domain.ChannelBean;

@Service
public class ChannelService {
    
    @Resource
    private ChannelMapper channelMapper;

    public int getCountChannel(Page<ChannelBean> page) {
        return channelMapper.getCountChannel(page);
    }
    
    public List<ChannelBean> getChannelList(Page<ChannelBean> page) {
        return channelMapper.getChannelList(page);
    }
    
    public int judgeCallsign(String callsign) {
        return channelMapper.judgeCallsign(callsign);
    }

    public int editChannel(ChannelBean channel) {
        return channelMapper.editChannel(channel);
    }

    public int addChannel(ChannelBean channel) {
        return channelMapper.addChannel(channel);
    }

    public ChannelBean getChannelById(int id) {
        return channelMapper.getChannelById(id);
    }

    public int onlineOrOffline(String ids, int status) {
        return channelMapper.onlineOrOffline(ids, status);
    }

    public int deleteChannelById(String ids) {
        return channelMapper.deleteChannelById(ids);
    }

    public void changeSequence(int id, int sequence) {
        channelMapper.changeSequence(id, sequence);
    }

    
}
