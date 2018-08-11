package com.merge.domain;

import java.io.Serializable;

public class Stream implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    //该文件用于查询mongo数据
    private StreamId _id;
    private String streamName;
    private String streamIcon;
    private String streamType;  //live,movie
    //private int type;           //0:samsat; 1:ms; 2:brothers tv; 3:orca iptv
    private String categoryname;
    private long createTimestamp;

    public Stream() {
        createTimestamp = System.currentTimeMillis();  
    }

    public Stream(String streamId, String streamName, String streamIcon, String type, String categoryId, String categoryName, String streamType) {
        setCreateTimestamp(System.currentTimeMillis());
        set_id(new StreamId(streamId, categoryId, type));
        setCategoryname(categoryname);
        setStreamIcon(streamIcon);
        setStreamName(streamName);
        setStreamType(streamType);
    }

    public Stream(String streamId, String categoryId, String type) {
        setCreateTimestamp(System.currentTimeMillis());
        set_id(new StreamId(streamId, categoryId, type));
    }

    public StreamId get_id() {
        return _id;
    }
    public void set_id(StreamId _id) {
        this._id = _id;
    }
    public String getStreamId() {
        return this._id.getStreamId();
    }
    public void setStreamId(String streamId) {
        this._id.setStreamId(streamId);
    }
    public String getStreamName() {
        return streamName;
    }
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }    
    public String getStreamIcon() {
        return streamIcon;
    }
    public void setStreamIcon(String streamIcon) {
        this.streamIcon = streamIcon;
    }
    public String getStreamType() {
        return streamType;
    }
    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }
    public String getCategoryId() {
        return _id.getCategoryId();
    }
    public void setCategoryId(String categoryId) {
        this._id.setCategoryId(categoryId);
    }
//    public int getType() {
//        return type;
//    }
//    public void setType(int type) {
//        this.type = type;
//    }
    public String getType() {
        return this._id.getType();
    }
    public void setType(String type) {
        this._id.setType(type);
    }
    public String getCategoryname() {
        return categoryname;
    }
    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    /**
     * @param createTimestamp the createTimestamp to set
     */
    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * @return the createTimestamp
     */
    public long getCreateTimestamp() {
        return createTimestamp;
    }
 
}
