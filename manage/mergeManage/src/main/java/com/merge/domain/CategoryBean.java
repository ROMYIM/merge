package com.merge.domain;

import java.io.Serializable;

public class CategoryBean implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private int type;
    private int status;
    private String language;
    private String description;
    private int parentid;
    private int snum;
    private int sequence;
    private int playlevel;
    
    private String parentname;
           
    public String getParentname() {
        return parentname;
    }
    public void setParentname(String parentname) {
        this.parentname = parentname;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getParentid() {
        return parentid;
    }
    public void setParentid(int parentid) {
        this.parentid = parentid;
    }
    public int getSnum() {
        return snum;
    }
    public void setSnum(int snum) {
        this.snum = snum;
    }
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public int getPlaylevel() {
        return playlevel;
    }
    public void setPlaylevel(int playlevel) {
        this.playlevel = playlevel;
    }
    
}
