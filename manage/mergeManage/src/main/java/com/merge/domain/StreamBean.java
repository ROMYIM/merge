package com.merge.domain;

import java.io.Serializable;

public class StreamBean implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String streamid;
    private String streamname;
    private String streamicon;
    private String categoryid;
    private String type;
    private int channelid;
    private int status;
    private String agreement;

    public StreamBean() {
        setStatus(1);
    }

    public StreamBean(String streamid, String categoryid, String type, int status) {
        super();
        setCategoryid(categoryid);
        setStatus(status);
        setStreamid(streamid);
        setType(type);
    }
        
    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreamid() {
        return streamid;
    }

    public void setStreamid(String streamid) {
        this.streamid = streamid;
    }

    public String getStreamname() {
        return streamname;
    }

    public void setStreamname(String streamname) {
        this.streamname = streamname;
    }
    public String getStreamicon() {
        return streamicon;
    }

    public void setStreamicon(String streamicon) {
        this.streamicon = streamicon;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChannelid() {
        return channelid;
    }

    public void setChannelid(int channelid) {
        this.channelid = channelid;
    }
    
}
