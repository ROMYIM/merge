package com.merge.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.merge.domain.AgreementAccountBean;
import com.merge.service.AgreementAccountService;

@Component
public class DynamicScheduleTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    
    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private AgreementAccountService aAccountService;
    
    private ScheduledFuture<?> future;

    private String cron = "";
    
    private String interval = "";

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
    
    public void setInterval(String interval) {
        this.interval = interval;
    } 
    
    public String getCron() {
        return cron;
    }

    //动态修改定时时间
    public void setCron(String ncron) {
        this.cron = ncron;
        stopCron();
        System.out.println(cron+"===============");
        future = threadPoolTaskScheduler.schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("动态修改定时任务cron参数，当前时间：" + dateFormat.format(new Date()));
                    
                    //多线程执行iptv数据请求
                    int offset = 0, limit = 4, page = 0; 
                    threadRun(offset, limit, page, mongoTemplate);
                    
                    //判断是否为当天最后一次执行时间(小时判断，需注意午夜12点为0点)
                    String[] cronArr = cron.split(" ");
                    if (Integer.parseInt(cronArr[2])+Integer.parseInt(interval) >= 24) {
                        int hour = Integer.parseInt(cronArr[2])+Integer.parseInt(interval) - 24;
                        cron = cronArr[0]+" "+cronArr[1]+" "+hour+" * * ?";
                    }else {
                        int hour = Integer.parseInt(cronArr[2])+Integer.parseInt(interval);
                        cron = cronArr[0]+" "+cronArr[1]+" "+hour+" * * ?";
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error:定时失败");
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                if ("".equals(cron) || cron == null)
                    return null;
                CronTrigger trigger = new CronTrigger(cron);// 定时任务触发，可修改定时任务的执行周期
                Date nextExecDate = trigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        });
    }


    public void stopCron() {
        if (future != null) {
            future.cancel(true); //取消任务调度
        }
    }
    
    //判断定时器状态 
    public boolean getCronStatus() {
        if(future != null) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * 
     * @MethodName:threadRun
     * @Description:定时任务主体，根据账号进行http请求
     * @author Windy
     * @date 2018年4月25日  下午5:33:41
     */
    private String threadRun(int offset, int limit, int page, MongoTemplate mongoTemplate) {
        String result="fail", code="", mac="", sn="", type="";
        int id = 0;
        try { 
            List<AgreementAccountBean> list = aAccountService.getAccountLimitList(offset,limit);
            if (list != null && list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    id = list.get(i).getId();
                    code = list.get(i).getCode();
                    mac = list.get(i).getMac();
                    sn = list.get(i).getSn();
                    type = list.get(i).getType();
                    MyThread thread = new MyThread(id, code, mac, sn, type, mongoTemplate);
                    thread.start();
                    //System.out.println(thread.res+"-----------");
                    Thread.sleep(2000); // 主线程延迟2秒 
                    thread.exit = true;  // 终止线程thread 
                    thread.join(); 
                    System.out.println("线程退出!"); 
                }
                page ++;
                offset = page * limit;
                System.out.println(offset+"--------offset---");
                threadRun(offset, limit, page, mongoTemplate);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
