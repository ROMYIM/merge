package com.merge.domain;

import java.io.Serializable;

/**
 * 用于mongo中存在播放链接，一对多
 *
 */
public class PlayUrlBean implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String streamid; //stream中存储节目的id，不是节目id
    private String playurl;
    private String userAgent;  //ms播放使用user-agent
    private int flag;  //用于标志该播放链接是否已被请求使用,0：未使用，1：已使用，默认0
    private String code;  //code, sn, type用于标识播放链接，以便于账号无效时清除该链接
    private String sn;
    private String type;
    private String userid;   //用于绑定用户的时候设置
    private String mac; //platinum协议中需要
    
    public String getStreamid() {
        return streamid;
    }
    public void setStreamid(String streamid) {
        this.streamid = streamid;
    }
    public String getPlayurl() {
        return playurl;
    }
    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
       
}
