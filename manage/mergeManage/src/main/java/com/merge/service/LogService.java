package com.merge.service;

import java.util.HashMap;
import java.util.Map;

import com.merge.dao.LogDAO;
import com.merge.domain.LogBean;
import com.merge.domain.UpdateRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LogService
 */
@Service
public class LogService {

    private final LogDAO logDAO;

    @Autowired
    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public void addLog(LogBean logBean) {
        logDAO.insertLog(logBean);
    }

    public LogBean initLogBean(String agreementType) {
        LogBean logBean = new LogBean(agreementType);
        Map<String, UpdateRecord> recordMap = new HashMap<>();
        recordMap.put("stream", new UpdateRecord());
        recordMap.put("playUrlBean", new UpdateRecord());
        logBean.setRecordMap(recordMap);
        return logBean;
    }

    public void setInsertCount(LogBean logBean, String collectionName, int count) {
        int insertCount = logBean.getRecordMap().get(collectionName).getInsertCount();
        insertCount += count;
        logBean.getRecordMap().get(collectionName).setInsertCount(insertCount);
    }

    public void setDeleteCount(LogBean logBean, String collectionName, int count) {
        int deleteCount = logBean.getRecordMap().get(collectionName).getDeleteCount();
        deleteCount += count;
        logBean.getRecordMap().get(collectionName).setDeleteCount(deleteCount);
    }

    public void setUpdateCount(LogBean logBean, String collectionName, int count) {
        int updateCount = logBean.getRecordMap().get(collectionName).getUpdateCount();
        updateCount += count;
        logBean.getRecordMap().get(collectionName).setUpdateCount(updateCount);
    }
}