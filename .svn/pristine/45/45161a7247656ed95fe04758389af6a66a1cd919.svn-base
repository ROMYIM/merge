package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.domain.ImagesBean;

@Mapper
public interface ImagesMapper {

    void addImages(ImagesBean images);
    
    void editImages(ImagesBean images);

    ImagesBean getPtInfoById(@Param("pid")int pid);

    List<ImagesBean> getPtByChannelid(@Param("channelid")int channelid);

    void delPtByIds(@Param("ids")String ids);

    List<ImagesBean> getPtByIds(@Param("ids")String ids);

    void delPtByChannelids(@Param("ids")String ids);

    List<ImagesBean> getPtByChannelids(@Param("channelids")String channelids, @Param("addtype")int addtype);

}
