package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.LanguageBean;

@Mapper
public interface LanguageMapper {

    int getCountLanguage();

    List<LanguageBean> getLanguageList(Page<LanguageBean> page);

    int judgeLanguage(@Param("name")String name);

    LanguageBean getLanguageById(@Param("id")int id);

    int editLanguage(LanguageBean language);

    int addLanguage(LanguageBean language);

    void delLanguage(@Param("ids")String ids);

    List<LanguageBean> getAllLanguage();

}
