package com.merge.domain;

import java.io.Serializable;

/**
 * UpdateRecord
 * 定时记录中对于每个集合的插入和删除记录
 */
public class UpdateRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private int insertCount;
    private int deleteCount;
    private int updateCount;

    /**
     * @param deleteCount the deleteCount to set
     */
    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    /**
     * @return the deleteCount
     */
    public int getDeleteCount() {
        return deleteCount;
    }

    /**
     * @param insertCount the insertCount to set
     */
    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    /**
     * @return the insertCount
     */
    public int getInsertCount() {
        return insertCount;
    }

    /**
     * @param updateCount the updateCount to set
     */
    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * @return the updateCount
     */
    public int getUpdateCount() {
        return updateCount;
    }

    public UpdateRecord() {
        insertCount = 0;
        deleteCount = 0;
        updateCount = 0;
    }
}