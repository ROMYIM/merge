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
import com.merge.domain.MergeGroupBean;
import com.merge.service.MergeGroupService;

@Controller
public class MergeGroupController {
    
    @Resource
    private MergeGroupService mergeGroupService;
    
    @RequestMapping("mergegroup")
    public String mergeGroup() {
        return "merge/mergegroup";
    }
    
    @RequestMapping("getMergeGroupList")
    @ResponseBody
    public String getMergeGroupList(@RequestParam("limit")int limit, @RequestParam("offset")int offset,
            @RequestParam("keyword")String keyword, @RequestParam("sort")String sort,
            @RequestParam("order")String order) {
        Page<MergeGroupBean> page = new Page<MergeGroupBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setQuery(keyword);
        page.setSortName(sort);
        page.setOrder(order);
        try {
            page.setTotal(mergeGroupService.getCountMergeGroup(keyword));
            List<MergeGroupBean> rows = mergeGroupService.getMergeGroupList(page);
            for(MergeGroupBean group : rows) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(group.getCreatetime());
                group.setCreatetime(sdf.format(date));
            }
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    @RequestMapping("mergeGroupManage")
    @ResponseBody
    public String mergeGroupManage(@RequestParam("gid")int gid, @RequestParam("name")String name, 
            @RequestParam("description")String description){
        JSONObject result = new JSONObject();
        String flag = "fail";
        int num = 0;
        try {
            MergeGroupBean group = new MergeGroupBean();
            group.setName(name);
            group.setDescription(description);
            if(gid != 0) {
                group.setId(gid);
                num = mergeGroupService.editMergeGroup(group);
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                group.setCreatetime(sdf.format(date));
                num = mergeGroupService.addMergeGroup(group);
            }
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("num", num);
        result.put("message", flag);
        return result.toJSONString();
    }
    
    @RequestMapping("/getMergeGroupById")
    @ResponseBody
    public String getMergeGroupById(@RequestParam("id")int id) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            MergeGroupBean group = mergeGroupService.getMergeGroupById(id);
            result.put("name", group.getName());
            result.put("description", group.getDescription());
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    
    @RequestMapping("/delMergeGroup")
    @ResponseBody
    public String delMergeGroup(@RequestParam("ids")String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            mergeGroupService.delMergeGroup(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    
    @RequestMapping("/judgeMergeGroup")
    @ResponseBody
    public int judgeMergeGroup(@RequestParam("name")String name) {
        int result = 0;
        try {
            result = mergeGroupService.judgeMergeGroup(name);    
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    @RequestMapping("/getAllMergeGroup")
    @ResponseBody
    public String getAllMergeGroup() {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            List<MergeGroupBean> list = mergeGroupService.getAllMergeGroup();           
            flag = "success";
            result.put("data", list);
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
}
