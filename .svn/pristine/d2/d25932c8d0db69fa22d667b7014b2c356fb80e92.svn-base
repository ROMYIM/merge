package com.merge.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.merge.domain.CategoryBean;
import com.merge.service.CategoryAndChannelService;
import com.merge.service.CategoryService;

@Controller
public class CategoryController {
    
    @Resource
    private CategoryService categoryService;
    
    @Resource
    private CategoryAndChannelService categoryAndChannelService;
    
    @RequestMapping("/category")  
    public String main() {
        return "category/category";
    }
    
    /**
     * 
     * @MethodName:categoryManage
     * @Description:添加或编辑分类
     * @author Windy
     * @date 2018年1月17日  下午12:19:29
     */
    @RequestMapping("/categoryManage")
    @ResponseBody
    public int categoryManage(@RequestParam(value="cid")int cid, @RequestParam(value="pid")int pid,
            @RequestParam(value="categoryname")String categoryname, @RequestParam(value="type")int type,
            @RequestParam(value="status")int status, @RequestParam(value="language")String language,
            @RequestParam(value="description")String description, @RequestParam(value="sequence")int sequence,
            @RequestParam(value="playlevel")int playlevel) {
        CategoryBean category = new CategoryBean();
        category.setName(categoryname);
        category.setType(type);
        category.setStatus(status);
        category.setLanguage(language);
        category.setDescription(description);
        category.setSequence(sequence);
        category.setPlaylevel(playlevel);
        if (pid != 0){
            category.setParentid(pid);
        }
        if (cid != 0) {
            category.setId(cid);
            return categoryService.editCategory(category);
        }else{
            return categoryService.addCategory(category);
        }
    }
    
    @RequestMapping("getCategoryById")
    @ResponseBody
    public String getCategoryById(@RequestParam("categoryid") int categoryid){
        CategoryBean category = categoryService.getCategoryById(categoryid);
        Map<String, Object> map = new HashMap<String, Object>();
        int parentType = -1;  //若为顶级分类，则不限制，-1表示no limit
        map.put("name", category.getName());
        map.put("type", category.getType());
        map.put("status", category.getStatus());
        map.put("language", category.getLanguage());
        map.put("description", category.getDescription());
        map.put("sequence", category.getSequence());
        map.put("playlevel", category.getPlaylevel());
        if(category.getParentid() != 0) {
            //返回父级分类的type
            parentType = categoryService.getTypeById(category.getParentid());
        }
        map.put("parentType", parentType);
        return JSONObject.toJSON(map).toString();
    }
    
    @RequestMapping("judgeByName")
    @ResponseBody
    public int judgeByName(@RequestParam("name")String name){
        int result = 0;
        try {
            CategoryBean ca = categoryService.judgeByName(name);
            if (ca != null) {
                result = 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * @MethodName:delCategory
     * @Description:删除分类
     * @author Windy
     * @date 2018年1月17日  下午12:18:54
     */
    @RequestMapping("/delCategory")
    @ResponseBody
    public int delCategory(@RequestParam("ids")String ids){
        int result = 0;
        try {
            String[] cids = ids.split(",");
            for (String cid : cids) {
                String re = getCategoryAndChildren(cid);
                categoryService.delCategoryAndChildren(re);
                //删除分类和节目的关联
                categoryAndChannelService.delByCategoryid(re);
            }
            result = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
     
    /** 
     * 返回 treeGrid(树形表格)需要的json格式数据 
     */ 
    @RequestMapping("/getCList")
    @ResponseBody
    public String getCList(){  
        //得到所有分类 
        List<CategoryBean> list = categoryService.getCList();  
        for (CategoryBean category : list) {
            if (category.getParentid() != 0) {
                CategoryBean categoryBean = categoryService.getCategoryById(category.getParentid());
                category.setParentname(categoryBean.getName());  
            }else {
                category.setParentname("Root");
            }
        }
        //调用方法实现分类树  
        String json = createTreeGridTree(list,"0"); 
        
        return json;  
    }  
      
      
    /** 
     * 将分类封装成树开始 
     * @param list 
     * @param parentid 父id 
     */  
    private String createTreeGridTree(List<CategoryBean> list, String fid) {  
        List<Map<String,Object>> treeGridList = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < list.size(); i++) {  
            Map<String, Object> map = null;  
            CategoryBean category = (CategoryBean) list.get(i);  
            if (category.getParentid() == 0) {  
                map = new HashMap<String, Object>();  
                //这里无所谓怎么转都行，因为在页面easyUI插件treeGrid提供了数据转换的columns属性，具体看相关的js代码  
                map.put("id", list.get(i).getId());
                map.put("name", list.get(i).getName());    
                map.put("parentid", list.get(i).getParentid());
                map.put("type", list.get(i).getType());
                map.put("status", list.get(i).getStatus());
                map.put("language", list.get(i).getLanguage());
                map.put("snum", list.get(i).getSnum());
                map.put("description", list.get(i).getDescription());
                map.put("sequence", list.get(i).getSequence());
                map.put("parentname", list.get(i).getParentname());
                map.put("children", createTreeGridChildren(list, category.getId()));  
            }  
            if (map != null)  
                treeGridList.add(map);  
        }  
        return JSONObject.toJSONString(treeGridList);
    }  
      
      
    /** 
     * 递归设置category树 
     * @param list 
     * @param parentid 
     * @return 
     */  
    private List<Map<String, Object>> createTreeGridChildren(List<CategoryBean> list, int fid) {  
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();  
        for (int j = 0; j < list.size(); j++) {  
            Map<String, Object> map = null;  
            CategoryBean treeChild = (CategoryBean) list.get(j);  
            if (treeChild.getParentid()==fid) {  
                map = new HashMap<String, Object>();  
                //这里无所谓怎么转都行，因为在页面easyUI插件treeGrid提供了数据转换的columns属性，具体看相关的js代码  
                map.put("id", list.get(j).getId());  
                map.put("name", list.get(j).getName());
                map.put("parentid", list.get(j).getParentid());
                map.put("type", list.get(j).getType());
                map.put("status", list.get(j).getStatus());
                map.put("language", list.get(j).getLanguage());
                map.put("snum", list.get(j).getSnum());
                map.put("description", list.get(j).getDescription());
                map.put("sequence", list.get(j).getSequence());
                map.put("parentname", list.get(j).getParentname());
                map.put("children", createTreeGridChildren(list, treeChild.getId()));  
            }  
              
            if (map != null)  
                childList.add(map);  
        }  
        return childList;  
    }  

    /**
     * 
     * @MethodName:getCategoryAndChildren
     * @Description:获取当前节点及子节点id
     * @author Windy
     * @date 2018年1月17日  下午12:16:30
     */
    private String getCategoryAndChildren(String parentId){
        String[] categoryIds =  categoryService.getCategorysByParentId(parentId);
        String result = parentId;
        for (String id : categoryIds) {
            result += "," + getCategoryAndChildren(id) ;
        }
        return result;  
    }
    
    /**
     * 
     * @MethodName:getCategoryName
     * @Description:获取分类用于channel中查询
     * @author Windy
     * @date 2018年4月13日  上午11:38:08
     */
    @RequestMapping("/getCategoryName")
    @ResponseBody
    public String getCategoryName() {
        //得到所有分类 
        List<CategoryBean> list = categoryService.getCList();  
        //调用方法实现分类树  
        String json = createTreeGridTrees(list,"0"); 
        String str = "\"nodes\":\\[\\]\\,";
        String newjson = json.replaceAll(str,"");
        return newjson;
    }
    
    private String createTreeGridTrees(List<CategoryBean> list, String fid) {  
        List<Map<String,Object>> treeGridList = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < list.size(); i++) {  
            Map<String, Object> map = null;  
            CategoryBean category = (CategoryBean) list.get(i);  
            if (category.getParentid() == 0) {  
                map = new HashMap<String, Object>();  
                map.put("id", list.get(i).getId());
                map.put("text", list.get(i).getName());
                map.put("nodes", createTreeGridChildrens(list, category.getId()));
            }  
            if (map != null)  
                treeGridList.add(map);  
        }  
        return JSONObject.toJSONString(treeGridList);
    }  
    
    private List<Map<String, Object>> createTreeGridChildrens(List<CategoryBean> list, int fid) {  
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();  
        for (int j = 0; j < list.size(); j++) {  
            Map<String, Object> map = null;  
            CategoryBean treeChild = (CategoryBean) list.get(j);  
            if (treeChild.getParentid()==fid) {  
                map = new HashMap<String, Object>();  
                map.put("id", list.get(j).getId());  
                map.put("text", list.get(j).getName());
                map.put("nodes", createTreeGridChildrens(list, treeChild.getId())); 
            }  
            if (map != null)  
                childList.add(map);  
        }  
        return childList;  
    }
    
    
    @RequestMapping("/getTopCategory")
    @ResponseBody
    public String getTopCategory(){
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            List<CategoryBean> list = categoryService.getTopCategory();           
            flag = "success";
            result.put("data", list);
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
}
