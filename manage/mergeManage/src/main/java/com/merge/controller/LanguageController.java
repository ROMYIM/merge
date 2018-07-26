package com.merge.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.Page;
import com.merge.domain.LanguageBean;
import com.merge.service.LanguageService;

@Controller
public class LanguageController {
    
    @Resource
    private LanguageService languageService;
    
    @RequestMapping("/language")
    public String language() {
        return "language/language";
    }
    
    @RequestMapping("/getLanguageList")
    @ResponseBody
    public String getLanguageList(@RequestParam("offset")int offset, @RequestParam("limit")int limit,
            @RequestParam("sort")String sort, @RequestParam("order")String order) {
        Page<LanguageBean> page = new Page<LanguageBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setSortName(sort);
        page.setOrder(order);
        try {
            page.setTotal(languageService.getCountLanguage());
            List<LanguageBean> rows = languageService.getLanguageList(page);
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    @RequestMapping("/judgeLanguage")
    @ResponseBody
    public int judgeLanguage(@RequestParam("name")String name) {
        int result = 0;
        try {
            result = languageService.judgeLanguage(name);    
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    @RequestMapping("/getLanguageById")
    @ResponseBody
    public String getLanguageById(@RequestParam("id")int id) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            LanguageBean language = languageService.getLanguageById(id);
            result.put("name", language.getName());
            result.put("sequence", language.getSequence());
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/languageManage")
    @ResponseBody
    public int languageManage(@RequestParam("lid")int lid, @RequestParam("name")String name,
            @RequestParam("sequence")int sequence) {
        try {
            LanguageBean language = new LanguageBean();
            language.setName(name);
            language.setSequence(sequence);
            if(lid != 0) {
                language.setId(lid);
                return languageService.editLanguage(language);
            }else {
                return languageService.addLanguage(language);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
    @RequestMapping("/delLanguage")
    @ResponseBody
    public String delLanguage(@RequestParam("ids") String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            languageService.delLanguage(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/getAllLanguage")
    @ResponseBody
    public String getAllLanguage() {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            List<LanguageBean> list = languageService.getAllLanguage();           
            flag = "success";
            result.put("data", list);
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
}
