package com.merge.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * LogBean
 * 定时任务的更新日志
 */
@Document(collection = "log")
public class LogBean implements Serializable {

    private static final long serialVersionUID = 1L;
	private String agreementType;                  //协议类型
    private Map<String, UpdateRecord> recordMap;   //更新的记录
    private String operateTime;                    //操作的时间

    /**
     * @param agreementType the agreementType to set
     */
    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    /**
     * @return the agreementType
     */
    public String getAgreementType() {
        return agreementType;
    }

    /**
     * @param operateTime the operateTime to set
     */
    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the operateTime
     */
    public String getOperateTime() {
        return operateTime;
    }

    /**
     * @param recordMap the recordMap to set
     */
    public void setRecordMap(Map<String, UpdateRecord> recordMap) {
        this.recordMap = recordMap;
    }

    /**
     * @return the recordMap
     */
    public Map<String, UpdateRecord> getRecordMap() {
        return recordMap;
    }

    public LogBean(String agreementType) {
        this.agreementType = agreementType;
        this.operateTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());
    }

    public LogBean() {
        
    }
}