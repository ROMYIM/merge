package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.LanguageMapper;
import com.merge.domain.LanguageBean;

@Service
public class LanguageService {

    @Resource
    private LanguageMapper languageMapper;
    
    public int getCountLanguage() {
        return languageMapper.getCountLanguage();
    }

    public List<LanguageBean> getLanguageList(Page<LanguageBean> page) {
        return languageMapper.getLanguageList(page);
    }

    public int judgeLanguage(String name) {
        return languageMapper.judgeLanguage(name);
    }

    public LanguageBean getLanguageById(int id) {
        return languageMapper.getLanguageById(id);
    }

    public int editLanguage(LanguageBean language) {
        return languageMapper.editLanguage(language);
    }

    public int addLanguage(LanguageBean language) {
        return languageMapper.addLanguage(language);
    }

    public void delLanguage(String ids) {
        languageMapper.delLanguage(ids);
    }

    public List<LanguageBean> getAllLanguage() {
        return languageMapper.getAllLanguage();
    }

}
