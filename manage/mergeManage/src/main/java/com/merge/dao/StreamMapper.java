package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.StreamBean;

@Mapper
public interface StreamMapper {

    int getCountStream(@Param("channelid")int channelid, @Param("keyword")String keyword);

    List<StreamBean> getStreamList(Page<StreamBean> page);

    void addStream(List<StreamBean> list);

    void delStream(@Param("sids")String sids);

    Integer getStream(StreamBean stream);

    void delStreamByChannelids(@Param("ids")String ids);

    void deleteStreamRelation(@Param("streamid")String streamid, @Param("categoryid")String categoryid, @Param("type")String type);

}
