package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.CategoryAndChannelBean;

@Mapper
public interface CategoryAndChannelMapper {

    Integer JudgeChannelExist(CategoryAndChannelBean c);

    void addChannel(List<CategoryAndChannelBean> list);

    int getCountCateAndChannel(@Param("cid")int cid, @Param("keyword")String keyword, @Param("status")int status);

    List<CategoryAndChannelBean> getCateAndChannelList(Page<CategoryAndChannelBean> page);

    void delProgram(@Param("channelids")String channelids, @Param("cid")int cid);

    void delChannel(@Param("ids")String ids);

    void delByCategoryid(@Param("categoryids")String categoryids);

    List<CategoryAndChannelBean> getCategoryIdByChannels(@Param("channelids")String channelids);

}
