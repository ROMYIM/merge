package com.merge.domain;

import java.io.Serializable;

public class AgreementAccountBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String code;
    private String mac;
    private String sn;
    private String type;
    private int status; //account status
    private int sequence;
    private String errorstr;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
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
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public String getErrorstr() {
        return errorstr;
    }
    public void setErrorstr(String errorstr) {
        this.errorstr = errorstr;
    }
    
}
