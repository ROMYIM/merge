package com.merge.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.Page;
import com.merge.domain.MergeUserBean;
import com.merge.service.CategoryService;
import com.merge.service.MergeUserService;
import com.merge.util.AesUserPass;

@Controller
public class MergeUserController {

    @Resource
    private MergeUserService mergeUserService;
    
    @Resource
    private CategoryService categoryService;
    
    @RequestMapping("mergeuser")
    public String mergeUser() {
        return "merge/mergeuser";
    }
    
    @RequestMapping("/getMergeUserList")
    @ResponseBody
    public String getMergeUserList(@RequestParam("limit")int limit, @RequestParam("offset")int offset,
            @RequestParam("keyword")String keyword, @RequestParam("sort")String sort,
            @RequestParam("order")String order, @RequestParam("status")int status) {
        Page<MergeUserBean> page = new Page<MergeUserBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setQuery(keyword);
        page.setSortName(sort);
        page.setOrder(order);
        page.setStatus(status);
        try {
            page.setTotal(mergeUserService.getCountMergeUser(keyword, status));
            List<MergeUserBean> rows = mergeUserService.getMergeUserList(page);
            for(MergeUserBean user : rows) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(user.getRegistertime());
                user.setRegistertime(sdf.format(date));
                if(user.getEffectivetime() != null) {
                    Date effectdate = sdf.parse(user.getEffectivetime());
                    user.setEffectivetime(sdf.format(effectdate));
                }
                String categoryname = categoryService.getCnameById(user.getCategory());
                user.setCategoryname(categoryname);
            }
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    @RequestMapping("/mergeUserManage")
    @ResponseBody
    public int mergeUserManage(@RequestParam("uid")int uid, @RequestParam("userid")String userid,
            @RequestParam("categoryid")int categoryid, @RequestParam("password")String password,
            @RequestParam("status")int status, @RequestParam("mergegroupid")int mergegroupid, 
            @RequestParam("effectivetime")String effectivetime) {
        String pwd = new AesUserPass().encrypt(password);
        MergeUserBean user = new MergeUserBean();
        user.setUserid(userid);
        user.setCategory(categoryid);
        user.setPassword(pwd);
        user.setStatus(status);
        user.setMergegroupid(mergegroupid);
        if (!effectivetime.equals("")) {
            user.setEffectivetime(effectivetime);
        }
        if (uid != 0) {
            user.setId(uid);
            return mergeUserService.editMergeUser(user);
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            user.setRegistertime(sdf.format(date));
            return mergeUserService.addMergeUser(user);
        }
    }
    
    @RequestMapping("/getMergeUserById")
    @ResponseBody
    public String getMergeUserById(@RequestParam("id")int id) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            MergeUserBean user = mergeUserService.getMergeUserById(id);
            //String pwd = new AesUserPass().decrypt(user.getPassword());
            result.put("userid", user.getUserid());
            result.put("category", user.getCategory());
            result.put("password", "1111"); //要根据length来返回吗？
            result.put("status", user.getStatus());
            result.put("mergegroupid", user.getMergegroupid());
            if (user.getEffectivetime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(user.getEffectivetime());
                result.put("effectivetime", sdf.format(date));
            }else {
                result.put("effectivetime", "");
            }
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/delMergeUserById")
    @ResponseBody
    public String delMergeUserById(@RequestParam("ids")String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            mergeUserService.delMergeUserById(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/judgeMergeUserid")
    @ResponseBody
    public int judgeMergeUserid(@RequestParam("userid")String userid) {
        int result = 0;
        try {
            result = mergeUserService.judgeMergeUserid(userid);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
