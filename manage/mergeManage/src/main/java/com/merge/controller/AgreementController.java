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
import com.merge.domain.AgreementBean;
import com.merge.service.AgreementService;

@Controller
public class AgreementController {
    
    @Resource
    private AgreementService agreementService;
    
    @RequestMapping("agreementcontrol")
    public String agreementControl() {
        return "agreement/agreementcontrol";
    }
    
    @RequestMapping("/getAgreementList")
    @ResponseBody
    public String getAgreementList(@RequestParam("offset")int offset, @RequestParam("limit")int limit,
            @RequestParam("sort")String sort, @RequestParam("order")String order) {
        Page<AgreementBean> page = new Page<AgreementBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setSortName(sort);
        page.setOrder(order);
        try {
            page.setTotal(agreementService.getCountAgreement());
            List<AgreementBean> rows = agreementService.getAgreementList(page);
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    @RequestMapping("/getAgreementById")
    @ResponseBody
    public String getAgreementById(@RequestParam("id")int id) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            AgreementBean agreement = agreementService.getAgreementById(id);
            result.put("name", agreement.getName());
            result.put("sequence", agreement.getSequence());
            result.put("description", agreement.getDescription());
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/agreementManage")
    @ResponseBody
    public int agreementManage(@RequestParam("aid")int aid, @RequestParam("name")String name,
            @RequestParam("sequence")int sequence, @RequestParam("description")String description) {
        try {
            AgreementBean agreement = new AgreementBean();
            agreement.setName(name);
            agreement.setSequence(sequence);
            agreement.setDescription(description);
            if(aid != 0) {
                agreement.setId(aid);
                return agreementService.editAgreement(agreement);
            }else {
                return agreementService.addAgreement(agreement);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
    @RequestMapping("/delAgreement")
    @ResponseBody
    public String delAgreement(@RequestParam("ids") String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            agreementService.delAgreement(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/getAllAgreement")
    @ResponseBody
    public String getAllAgreement() {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            List<AgreementBean> list = agreementService.getAllAgreement();           
            flag = "success";
            result.put("data", list);
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
}
