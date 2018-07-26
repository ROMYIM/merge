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
import com.merge.domain.AgreementAccountBean;
import com.merge.service.AgreementAccountService;

@Controller
public class AgreementAccountController {
    
    @Resource
    private AgreementAccountService agreementAccountService;

    @RequestMapping("agreementaccount")
    public String agreementAccount() {
        return "agreement/agreementaccount";
    }
    
    @RequestMapping("/agreementAccountList")
    @ResponseBody
    public String agreementAccountList(@RequestParam("offset")int offset, 
            @RequestParam("limit")int limit, @RequestParam("sort")String sort,
            @RequestParam("order")String order) {
        Page<AgreementAccountBean> page = new Page<AgreementAccountBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setSortName(sort);
        page.setOrder(order);
        try {
            page.setTotal(agreementAccountService.getCountAccount());
            List<AgreementAccountBean> rows = agreementAccountService.getAccountList(page);
            page.setRows(rows);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    @RequestMapping("/agreementAccountManage")
    @ResponseBody
    public int agreementAccountManage(@RequestParam("code")String code, 
            @RequestParam("sequence")int sequence, @RequestParam("mac")String mac, 
            @RequestParam("sn")String sn, @RequestParam("agreement")String agreement) {
        try {
            AgreementAccountBean account = new AgreementAccountBean();
            account.setCode(code);
            account.setMac(mac);
            account.setSn(sn);
            account.setType(agreement);     
            account.setSequence(sequence);
            return agreementAccountService.addAccount(account);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @RequestMapping("/delAgreementAccount")
    @ResponseBody
    public String delAgreementAccount(@RequestParam("ids")String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            agreementAccountService.delAccount(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
}
