package com.merge.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.FtpFileUtil;
import com.merge.config.Page;
import com.merge.domain.CategoryAndChannelBean;
import com.merge.domain.ChannelBean;
import com.merge.domain.ImagesBean;
import com.merge.service.CategoryAndChannelService;
import com.merge.service.CategoryService;
import com.merge.service.ChannelService;
import com.merge.service.ImagesService;
import com.merge.service.StreamService;

@Controller
public class ChannelController {

    @Resource
    private ChannelService channelService;
    
    @Resource
    private CategoryService categoryService;
    
    @Resource
    private CategoryAndChannelService categoryAndChannelService;
    
    @Resource
    private ImagesService imagesService;
    
    @Resource
    private StreamService streamService;
    
    @RequestMapping("/channel")
    public String live(){
        return "channel/channel";
    }
    
    /**
     * 
     * @MethodName:getChannel
     * @Description:获取节目列表
     * @author Windy
     * @date 2018年3月26日  下午6:23:42
     */
    @RequestMapping("/getChannel")
    @ResponseBody
    public String getChannel(@RequestParam("offset")int offset, @RequestParam("limit")int limit,
            @RequestParam("keyword")String keyword, @RequestParam("status")int status,
            @RequestParam("type")int type, @RequestParam("language")String language,
            @RequestParam("scid")Integer scid) {
        Page<ChannelBean> page = new Page<ChannelBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setQuery(keyword);
        page.setStatus(status);
        page.setType(type);
        page.setLanguage(language);
        page.setCid(scid);
        try {
            page.setTotal(channelService.getCountChannel(page));
            List<ChannelBean> rows = channelService.getChannelList(page);
            for(ChannelBean channel : rows) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(channel.getCreatetime());
                channel.setCreatetime(sdf.format(date));
            }
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    /**
     * 
     * @MethodName:judgeCallsign
     * @Description:判断callsign是否唯一
     * @author Windy
     * @date 2018年3月26日  下午6:23:58
     */
    @RequestMapping("/judgeCallsign") 
    @ResponseBody
    public int judgeCallsign(@RequestParam("callsign") String callsign) {
        int result = 0;
        try {
            result = channelService.judgeCallsign(callsign);    
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * @MethodName:channelManage
     * @Description:添加或编辑节目
     * @author Windy
     * @date 2018年3月26日  下午6:24:21
     */
    @RequestMapping("/channelManage")
    @ResponseBody
    public int channelManage(@RequestParam("cid")int cid, @RequestParam("name")String name, 
            @RequestParam("callsign")String callsign, @RequestParam("order")int order,
            @RequestParam("type")int type, @RequestParam("status")int status, 
            @RequestParam("language")String language, @RequestParam("description")String description) {
        try {
            ChannelBean channel = new ChannelBean();
            channel.setName(name);
            channel.setCallsign(callsign);
            channel.setSequence(order);
            channel.setType(type);
            channel.setStatus(status);
            channel.setLanguage(language);
            channel.setDescription(description);
            if(cid != 0) {
                channel.setId(cid);
                return channelService.editChannel(channel);
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                channel.setCreatetime(sdf.format(date));
                return channelService.addChannel(channel);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 
     * @MethodName:getChannelById
     * @Description:根据id查询channel信息
     * @author Windy
     * @date 2018年3月27日  上午9:30:39
     */
    @RequestMapping("/getChannelById")
    @ResponseBody
    public String getChannelById(@RequestParam("id") int id) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            ChannelBean channel = channelService.getChannelById(id);
            result.put("name", channel.getName());
            result.put("callsign", channel.getCallsign());
            result.put("order", channel.getSequence());
            result.put("type", channel.getType());
            result.put("status", channel.getStatus());
            result.put("language", channel.getLanguage());
            result.put("description", channel.getDescription());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(channel.getCreatetime());
            result.put("createtime", sdf.format(date));
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    /**
     * 
     * @MethodName:onlineOrOffline
     * @Description:上线或下线
     * @author Windy
     * @date 2018年3月27日  上午10:02:06
     */
    @RequestMapping("/onlineOrOffline")
    @ResponseBody
    public String onlineOrOffline(@RequestParam("ids") String ids, @RequestParam("status") int status) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            channelService.onlineOrOffline(ids, status);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
    /**
     * 
     * @MethodName:deleteChannelById
     * @Description:根据id删除channel
     * @author Windy
     * @date 2018年3月27日  上午10:02:23
     */
    @RequestMapping("/deleteChannelById")
    @ResponseBody
    public String deleteChannelById(@RequestParam("ids") String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            //修改分类的节目数目
            List<CategoryAndChannelBean> list = categoryAndChannelService.getCategoryIdByChannels(ids);
            if (list!=null && list.size()>0) {
                Map<Object, Integer> map = new HashMap<Object, Integer>();
                for(CategoryAndChannelBean cate : list) {
                    int categoryid = cate.getCategoryid();
                    if(map.get(categoryid)!=null){
                        map.put(categoryid,map.get(categoryid)+1);
                    }else{
                        map.put(categoryid, 1);
                    }
                }
                for (Entry<Object, Integer> entry : map.entrySet()) {  
                    String cids = getCategoryAndParent(String.valueOf(entry.getKey()));
                    String[] ncid = cids.split(",");
                    for (String id : ncid) {
                        categoryService.updateSnum(Integer.parseInt(id), -entry.getValue());
                    }
                }
            }
            //删除分类和节目的关联
            categoryAndChannelService.delChannel(ids);
            
            //查找是否有为ftp上传的图片，若有则去ftp删除
            List<ImagesBean> imglist = imagesService.getPtByChannelids(ids, 1);
            if (imglist != null) {
                for(ImagesBean images: imglist) {
                    FtpFileUtil.removeFile(images.getPicpath());
                }
            }
            //删除图片和节目的关联
            imagesService.delPtByChannelids(ids);
            
            //删除物理频道和节目的关联
            streamService.delStreamByChannelids(ids);
            
            //删除节目
            channelService.deleteChannelById(ids);
            
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toJSONString();
    }
    
      
    @RequestMapping("/changeSequence")
    @ResponseBody
    public String changeSequence(@RequestParam("id")int id, @RequestParam("sequence")int sequence) {
        String flag = "fail";
        try {
            channelService.changeSequence(id, sequence);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
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
