package com.iptv.domain;

import java.io.Serializable;

public class Stream implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String streamId;
    private String streamName;
    private String streamIcon;
    private String streamType;  //live,movie
    private String categoryId;
    private int type;           //0:samsat; 1:ms; 2:brothers tv; 3:orca iptv

    public String getStreamId() {
        return streamId;
    }
    public void setStreamId(String streamId) {
        this.streamId = streamId;
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
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
}
