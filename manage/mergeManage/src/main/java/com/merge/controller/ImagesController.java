package com.merge.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.merge.config.FtpFileUtil;
import com.merge.domain.ImagesBean;
import com.merge.service.ImagesService;

@Controller
public class ImagesController {
    
    @Resource
    private ImagesService imagesService;

    @RequestMapping(value="/addpicture", method=RequestMethod.POST)
    @ResponseBody
    public String addpicture(@RequestParam("pname") String picname, @RequestParam("radio") int addtype,
            @RequestParam("picType") int pictype, @RequestParam("description") String description,
            @RequestParam("fileImg") MultipartFile file, @RequestParam("pictureurl") String picurl,
            @RequestParam("channelid") int channelid, @RequestParam("pid")int pid){
        String flag = "fail";
        try {
            if(addtype == 1) {
                if (file != null) { 
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")); //获取文件的后缀 
                    String fileName = UUID.randomUUID().toString() + suffix; //使用UUID生成文件名称
                    InputStream inputStream = file.getInputStream();
                    String filePath = null;
                    String uploadresult = FtpFileUtil.uploadFile(fileName,inputStream);
                    JSONObject uploadobj = JSONObject.parseObject(uploadresult);
                    if (uploadobj.getString("message").equals("success")) {
                        filePath = uploadobj.getString("filePath");
                        ImagesBean images = new ImagesBean();
                        images.setPicname(picname);
                        images.setPicpath(filePath);
                        images.setPictype(pictype);
                        images.setDescription(description);
                        images.setChannelid(channelid);
                        images.setAddtype(addtype);
                        imagesService.addImages(images);
                        flag = "success";
                    }
                }
            }else if(addtype == 0) {
                ImagesBean images = new ImagesBean();
                images.setPicname(picname);
                images.setPicpath(picurl);
                images.setPictype(pictype);
                images.setDescription(description);
                images.setChannelid(channelid);
                images.setAddtype(addtype);
                if (pid > 0) {
                    images.setId(pid);
                    imagesService.editImages(images);
                }else {
                    imagesService.addImages(images);
                }
                flag = "success";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;      
    }
    
    @RequestMapping("/getPtInfoById")
    @ResponseBody
    public String getPtInfoById(@RequestParam("pid")int pid) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            ImagesBean images = imagesService.getPtInfoById(pid);
            result.put("picname", images.getPicname());
            result.put("picpath", images.getPicpath());
            result.put("description", images.getDescription());
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toString();
    }
    
    @RequestMapping("/getPtByChannelid")
    @ResponseBody
    public String getPtByChannelid(@RequestParam("channelid")int channelid) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
        try {
            List<ImagesBean> imglist = imagesService.getPtByChannelid(channelid);
            if (imglist != null) {
                for(ImagesBean images: imglist) {
                    Map<String, Object> map = new HashMap<String, Object>(); 
                    map.put("id", images.getId());
                    map.put("pname", images.getPicname());
                    map.put("ptype", images.getPictype());
                    list.add(map);
                }
            }
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null) {
            result.put("list", list);
        }
        result.put("message", flag);
        return result.toString();
    }
    
    @RequestMapping("/getPtByPicid")
    @ResponseBody
    public String getPtByPicid(@RequestParam("picid")int picid) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {
            ImagesBean images = imagesService.getPtInfoById(picid);
            result.put("id", images.getId());
            result.put("pname", images.getPicname());
            result.put("picpath", images.getPicpath());
            result.put("ptype", images.getPictype());
            result.put("description", images.getDescription());
            result.put("channelid", images.getChannelid());
            result.put("addtype", images.getAddtype());
            flag = "success";
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toString();
    }
    
    @RequestMapping("/delPtByIds")
    @ResponseBody
    public String delPtByIds(@RequestParam("ids")String ids) {
        JSONObject result = new JSONObject();
        String flag = "fail";
        try {  
            //用于删除使用ftp上传的图片
            List<ImagesBean> list = imagesService.getPtByIds(ids);
            if(list != null) {
                for(ImagesBean images : list) {
                    if (images.getAddtype() == 1) {
                        FtpFileUtil.removeFile(images.getPicpath());
                    }
                }
            }
            imagesService.delPtByIds(ids);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", flag);
        return result.toString();
    }
    
}
