package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.ChannelBean;

@Mapper
public interface ChannelMapper {
    
    int getCountChannel(Page<ChannelBean> page); 
    List<ChannelBean> getChannelList(Page<ChannelBean> page);

    int judgeCallsign(@Param("callsign") String callsign);

    int editChannel(ChannelBean channel);

    int addChannel(ChannelBean channel);

    ChannelBean getChannelById(@Param("id") int id);

    int onlineOrOffline(@Param("ids") String ids, @Param("status") int status);

    int deleteChannelById(@Param("ids") String ids);

    void changeSequence(@Param("id")int id, @Param("sequence")int sequence);
    
    
}
