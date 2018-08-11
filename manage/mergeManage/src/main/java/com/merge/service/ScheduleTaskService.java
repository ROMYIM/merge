package com.merge.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.merge.dao.AgreementAccountMapper;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.StreamBean;
import com.merge.util.ApplicationContextHelper;
import com.merge.util.FormateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/**
 * ScheduleTaskService
 */
@Service
public class ScheduleTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskService.class);

    private final PlayUrlService playUrlService;
    private final StreamService streamService;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final AgreementAccountMapper accountMapper;

    private ScheduledFuture<?> updateStreamFutture;
    private Map<String, String> intervalScheduleTimeMap;

    @Autowired
    public ScheduleTaskService(PlayUrlService playUrlService, StreamService streamService, ThreadPoolTaskExecutor taskExecutor, ThreadPoolTaskScheduler taskScheduler, AgreementAccountMapper accountMapper) {
        this.playUrlService = playUrlService;
        this.streamService = streamService;
        this.taskExecutor = taskExecutor;
        this.taskScheduler = taskScheduler;
        this.accountMapper = accountMapper;
        intervalScheduleTimeMap = new ConcurrentHashMap<>();
    }

    /**
     * @return the intervalScheduleTimeMap
     */
    public Map<String, String> getIntervalScheduleTimeMap() {
        return intervalScheduleTimeMap;
    }

    @Scheduled(initialDelay = 1000 * 15, fixedRate = 1000 * 3600)
    public void CheckStreamService() {
        LOGGER.info("check stream service start");
        streamService.clearOldStreams();
        playUrlService.clearOldPlayUrl();
        List<StreamBean> streamBeans = streamService.getStreamByProperties();
        if (streamBeans != null && streamBeans.size() > 0) {
            for (StreamBean streamBean : streamBeans) {
                streamService.updateStreamRelation(streamBean.getStreamid(), streamBean.getCategoryid(), streamBean.getType());
            }
        }
        LOGGER.info("check stream service end");
    }

    public void resetIntervalTask(String hour, String minute, String second, String interval) {
        try {
            stopTask(updateStreamFutture);
            long period = FormateUtil.stringToPeriod(interval);
            Date firstExecuteTime = FormateUtil.timeToDate(hour, minute, second);
            updateStreamFutture = taskScheduler.scheduleAtFixedRate(new Runnable(){
            
                @Override
                public void run() {
                    List<AgreementAccountBean> accountBeans = accountMapper.selectAllAccountByStatus();
                    if (accountBeans!= null && accountBeans.size() > 0) {
                        for (AgreementAccountBean accountBean : accountBeans) {
                            UpdateStreamInfoService updateStreamInfoService = getUpdateStreamInfoService(accountBean.getType());
                            LOGGER.info(accountBean.getType() + ": 更新节目信息-" + accountBean.getId());
                            updateStreamByAccount(accountBean, updateStreamInfoService);
                            LOGGER.info(accountBean.getType() + "：更新节目完成-" + accountBean.getId());
                        }
                    }
                    // List<String> typeList = accountMapper.selectAgreementTypeFromAccount();
                    // if (typeList != null && typeList.size() > 0) {
                    //     for (int i = 0; i < typeList.size(); i++) {
                    //         final String type = typeList.get(i);
                    //         taskExecutor.execute(new Runnable(){
                            
                    //             @Override
                    //             public void run() {
                    //                 List<AgreementAccountBean> accountBeans = accountMapper.selectAccountsByType(type);
                    //                 UpdateStreamInfoService updateStreamInfoService = getUpdateStreamInfoService(type);
                    //                 if (accountBeans != null && accountBeans.size() > 0) {
                    //                     for (AgreementAccountBean accountBean : accountBeans) {
                    //                         LOGGER.info(accountBean.getType() + ": 更新节目信息-" + accountBean.getId());
                    //                         updateStreamByAccount(accountBean, updateStreamInfoService);
                    //                         LOGGER.info(accountBean.getType() + "：更新节目完成-" + accountBean.getId());
                    //                     }
                    //                 }
                    //             }
                    //         });
                    //     }
                    // }
                }
            }, firstExecuteTime, period);
            intervalScheduleTimeMap.put("taskHour", hour);
            intervalScheduleTimeMap.put("taskMinute", minute);
            intervalScheduleTimeMap.put("taskSecond", second);
            intervalScheduleTimeMap.put("taskInterval", interval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void stopTask(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(true);
            intervalScheduleTimeMap.put("taskHour", null);
            intervalScheduleTimeMap.put("taskMinute", null);
            intervalScheduleTimeMap.put("taskSecond", null);
            intervalScheduleTimeMap.put("taskInterval", null);
        }
    }

    public boolean getIntervalTaskStatus() {
        String hour = intervalScheduleTimeMap.get("taskHour");
        String minute = intervalScheduleTimeMap.get("taskMinute");
        String second = intervalScheduleTimeMap.get("taskInterval");
        if (hour != null && minute != null && second != null) {
            return true;
        }
        return false;
    }

    private UpdateStreamInfoService getUpdateStreamInfoService(final String type) {
        UpdateStreamInfoService updateStreamInfoService = null;
        switch (type) {
            case "brother":
                updateStreamInfoService = ApplicationContextHelper.getBean("brotherStream", BrotherStreamService.class);
                break;
            case "myhd":
                updateStreamInfoService = ApplicationContextHelper.getBean("myhdStream", MyhdStreamService.class);
                break;
            case "orca":
                updateStreamInfoService = ApplicationContextHelper.getBean("orcaStream", OrcaIptvStreamService.class);
                break;
            case "samsat":
                updateStreamInfoService = ApplicationContextHelper.getBean("samsatStream", SamsatStreamService.class);
                break;
            case "sham":
                updateStreamInfoService = ApplicationContextHelper.getBean("shamStream", ShamStreamService.class);
                break;
            case "Ms":
                updateStreamInfoService = ApplicationContextHelper.getBean("msStream", MsStreamService.class);
                break;
            case "platinum":
                updateStreamInfoService = ApplicationContextHelper.getBean("platinumStream", PlatinumStreamService.class);
                break;
            case "ferarri":
                updateStreamInfoService = ApplicationContextHelper.getBean("ferarriStream", FerarriStreamService.class);
                break;
            default:
                break;
        }
        return updateStreamInfoService;
    }

    private void updateStreamByAccount(AgreementAccountBean accountBean, UpdateStreamInfoService updateStreamInfoService) {
        try {
            Map<String, String> resourceStringMap = updateStreamInfoService.getRemoteResource(accountBean);
            if (accountBean.getStatus() == 0) {
                updateStreamInfoService.updateChannel(resourceStringMap, accountBean);
            } else {
                updateStreamInfoService.updateRealtion(accountBean);
            }
        } catch (Exception e) {
            accountBean.setStatus(-1);
            accountBean.setErrorstr("request fail");
            e.printStackTrace();
        } finally {
            accountMapper.setStatusAndError(accountBean);
        }
    }

}