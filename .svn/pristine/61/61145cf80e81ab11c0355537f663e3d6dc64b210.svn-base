package com.merge.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.merge.config.Page;
import com.merge.domain.CategoryAndChannelBean;
import com.merge.service.CategoryAndChannelService;
import com.merge.service.CategoryService;

@Controller
public class ProgramController {

    @Resource
    private CategoryAndChannelService categoryAndChannelService;
    
    @Resource
    private CategoryService categoryService;
    
    @RequestMapping("/program")
    public String channel() {
        return "category/program";
    }
    
    /**
     * 
     * @MethodName:getProgram
     * @Description:获取该分类下频道列表
     * @author Windy
     * @date 2018年3月27日  下午5:47:05
     */
    @RequestMapping("/getProgram")
    @ResponseBody
    public String getProgram(@RequestParam("offset")int offset, @RequestParam("limit")int limit,
            @RequestParam("cid")int cid, @RequestParam("keyword")String keyword,
            @RequestParam("sort")String sort, @RequestParam("order")String order, 
            @RequestParam("status")int status) {
        Page<CategoryAndChannelBean> page = new Page<CategoryAndChannelBean>();
        page.setCid(cid);
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setQuery(keyword);
        page.setSortName(sort);
        page.setOrder(order);
        page.setStatus(status);
        try {
            page.setTotal(categoryAndChannelService.getCountCateAndChannel(cid, keyword, status));
            List<CategoryAndChannelBean> rows = categoryAndChannelService.getCateAndChannelList(page);
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    /**
     * 
     * @MethodName:addProgramById
     * @Description:添加频道
     * @author Windy
     * @date 2018年3月27日  下午4:49:23
     */
    @RequestMapping("/addProgramById")
    @ResponseBody
    public String addProgramById(@RequestParam("cid")int cid, @RequestParam("ids")String ids){
        String result = "fail";
        try{
            List<CategoryAndChannelBean> list = new ArrayList<CategoryAndChannelBean>();
            String[] channelid = ids.split(",");
            for(int i=0; i<channelid.length; i++){
                CategoryAndChannelBean c = new CategoryAndChannelBean();
                c.setCategoryid(cid);
                c.setChannelid(Integer.parseInt(channelid[i]));
                Integer isExist = categoryAndChannelService.JudgeChannelExist(c);
                if(isExist == null){
                    list.add(c);
                }
            }
            if (list.size() >0){
                String cids = getCategoryAndParent(String.valueOf(cid));
                String[] ncid = cids.split(",");
                for (String id : ncid) {
                    categoryService.updateSnum(Integer.parseInt(id),list.size());
                }
                categoryAndChannelService.addChannel(list);
            }
            result = "success";
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    @RequestMapping("/delProgram")
    @ResponseBody
    public String delProgram(@RequestParam("channelids")String channelids, @RequestParam("cid")int cid) {
        String result = "fail";
        try{
            categoryAndChannelService.delProgram(channelids, cid);
            String[] idsarr = channelids.split(",");
            String cids = getCategoryAndParent(String.valueOf(cid));
            String[] ncid = cids.split(",");
            for (String id : ncid) {
                categoryService.updateSnum(Integer.parseInt(id), -idsarr.length);
            }
            result = "success";
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    private String getCategoryAndParent(String cid){
        String result = cid;
        String[] categoryIds = categoryService.getParentIdByCid(cid);
        for (String id : categoryIds) {
            if(!id.equals("0")){                        
                result += "," + getCategoryAndParent(id) ;
            }
        }        
        return result;  
    }
}
