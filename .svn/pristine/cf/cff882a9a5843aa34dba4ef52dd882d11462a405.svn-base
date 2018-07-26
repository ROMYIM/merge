package com.merge.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.merge.config.DynamicScheduleTask;
import com.merge.config.Page;
import com.merge.domain.AgreementAccountBean;
import com.merge.service.AgreementAccountService;

@Controller
public class AgreementTestController {
    
    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private AgreementAccountService aAccountService;
    
    @Resource
    DynamicScheduleTask task;
    
    @RequestMapping("agreementtest")
    public String agreementTest() {
        return "agreement/agreementtest";
    }
    
    /**
     * 
     * @MethodName:getStatusAndError
     * @Description: 返回账号异常信息
     * @author Windy
     * @date 2018年4月24日  下午4:55:44
     */
    @RequestMapping("/getStatusAndError")
    @ResponseBody
    public String getStatusAndError(@RequestParam("offset")int offset, 
            @RequestParam("limit")int limit, @RequestParam("status")int status,
            @RequestParam("type")String type) {
        Page<AgreementAccountBean> page = new Page<AgreementAccountBean>();
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setStatus(status);
        page.setQuery(type);  //协议名
        try {
            page.setTotal(aAccountService.getCountAccountStatus(status, type));
            List<AgreementAccountBean> rows = aAccountService.getAccountStatus(page);
            page.setRows(rows);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    /**
     * 
     * @MethodName:updateDynamicScheduledTask
     * @Description:动态更新定时器
     * @author Windy
     * @date 2018年4月25日  下午4:42:01
     */
    @RequestMapping("/updateDynamicScheduledTask")
    @ResponseBody
    public String updateDynamicScheduledTask(@RequestParam("hour")String hour, 
            @RequestParam("minute")String minute,
            @RequestParam("second")String second,
            @RequestParam("interval")String interval) {
        //第一次任务时间需从页面获取，重新访问界面，提示定时器已执行，若确认修改，则动态修改定时时间
        System.out.println(hour+":"+minute+":"+second+"----"+interval);
        String cron = second+" "+minute+" "+hour+" * * ?";
        task.setInterval(interval);
        task.setCron(cron);
        return "success";
    }
    
    /**
     * 
     * @MethodName:getScheduleStatus
     * @Description:获取定时器状态
     * @author Windy
     * @date 2018年4月25日  下午3:35:06
     */
    @RequestMapping("/getScheduleStatus")
    @ResponseBody
    public boolean getScheduleStatus() {
        boolean status = false;
        if (task.getCronStatus()) {
            status = true;
        }
        return status;
    }
    
}
