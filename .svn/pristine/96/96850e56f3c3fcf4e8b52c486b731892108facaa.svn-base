package com.iptv.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iptv.config.BaseLogin;
import com.iptv.config.HttpClientUtil;
import com.iptv.config.RedisClient;

@Controller
public class IndexController {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RedisClient redisClient;
            
    @RequestMapping("/")  
    public String index() {
        return "iptv";
    }
    
    @RequestMapping("/test")
    public String test() {
        return "test";
    } 
    
    /**
     * 
     * @MethodName:loginIptv
     * @Description:点击登录
     * @author Windy
     * @date 2017年12月6日  下午5:15:08
     */
    @RequestMapping("/loginIptv")
    @ResponseBody
    public String loginIptv(@RequestParam("code")String code, @RequestParam("sn")String sn,
            @RequestParam("type") int type, @RequestParam("streamType") int streamType) throws Exception{
        String flag="fail";
        try {
            redisClient.flushdb();
            if (0 == type) {
                BaseLogin login = new SamsatController();
                flag = login.baseLogin(code, sn, redisClient, mongoTemplate);
            }else if (1 == type) {
                BaseLogin login = new MsController();
                flag = login.baseLogin( code, sn, redisClient, mongoTemplate);
            }else if (2 == type) {
                BaseLogin login = new BrothersTvController();
                flag = login.baseLogin(code, sn, redisClient, mongoTemplate);
            }else if (3 == type) {
                BaseLogin login = new OrcalIptvController();
                if (0 == streamType) {  //请求直播
                    flag = login.baseLogin(code, sn, redisClient, mongoTemplate);
                }else {  //电影请求
                    flag = login.baseLogin(code, sn, streamType, redisClient, mongoTemplate);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
        
    /**
     * 
     * @MethodName:getChannelByCategoryid
     * @Description:根据分类id分页获取节目
     * @author Windy
     * @date 2017年12月29日  下午2:58:51
     */   
    @RequestMapping("getChannelByCid")
    @ResponseBody
    public String getChannelByCategoryid(HttpServletRequest request, HttpServletResponse response, 
            @RequestParam("categoryid")String categoryid,@RequestParam("code")String code,
            @RequestParam("type")int type, @RequestParam("streamType")int streamType){
        String results = "";
        Integer offset = Integer.parseInt(StringUtils.isEmpty(request.getParameter("page"))?"1":request.getParameter("page").trim());
        Integer limit = Integer.parseInt(StringUtils.isEmpty(request.getParameter("rows"))?"10":request.getParameter("rows").trim());
        try {
            if (0 == type) {
                BaseLogin login = new SamsatController();
                results = login.getChannelById(categoryid, offset, limit, redisClient); 
            }else if (1 == type) {
                BaseLogin login = new MsController();
                results = login.getChannelById(categoryid, offset, limit, redisClient);
            }else if (2 == type) {
                BaseLogin login = new BrothersTvController();  //需要用key重新请求，并存入redis和MongoDB
                results = login.getChannelById(categoryid, code, offset, limit, redisClient, mongoTemplate);               
            }else if (3 == type) {
                BaseLogin login = new OrcalIptvController();
                if (0 == streamType) {
                    results = login.getChannelById(categoryid, offset, limit, redisClient);
                }else {
                    results = login.getChannelById(categoryid, streamType, offset, limit, redisClient, mongoTemplate);
                }
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * 
     * @MethodName:getPlayUrl
     * @Description:获取user-agent用以播放 
     * @author Windy
     * @date 2018年3月22日  下午3:13:55
     */
    @RequestMapping("getPlayUrl")
    @ResponseBody
    public String getPlayUrl(@RequestParam("type") int type) {
        Map<String, Object> map = new HashMap<String, Object>(); 
        try {
            if (redisClient.exists("playUrl") && type == 1) {
                map.put("user_agent", redisClient.hget("playUrl", "user_agent"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toJSONString(map);
    }
    

    @RequestMapping("/testHttp")
    @ResponseBody
    public String testHttp(@RequestParam("code")String code, @RequestParam("sn")String sn,
            @RequestParam("type") int type) {
        String flag = "fail", url = "";
        try {
            url = "http://act.smi-iptv.com/stbact/actnew.php?code="+code+"&stbid="+sn;
            String result = HttpClientUtil.sendHttpGet(url);
            System.out.println(result);
            flag = "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
