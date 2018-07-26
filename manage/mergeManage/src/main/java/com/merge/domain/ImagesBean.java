package com.merge.domain;

import java.io.Serializable;

public class ImagesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String picname;
    private String picpath;
    private int pictype;
    private String description;
    private int channelid;
    private int addtype;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPicname() {
        return picname;
    }
    public void setPicname(String picname) {
        this.picname = picname;
    }
    public String getPicpath() {
        return picpath;
    }
    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }
    public int getPictype() {
        return pictype;
    }
    public void setPictype(int pictype) {
        this.pictype = pictype;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getChannelid() {
        return channelid;
    }
    public void setChannelid(int channelid) {
        this.channelid = channelid;
    }
    public int getAddtype() {
        return addtype;
    }
    public void setAddtype(int addtype) {
        this.addtype = addtype;
    }
 
}
