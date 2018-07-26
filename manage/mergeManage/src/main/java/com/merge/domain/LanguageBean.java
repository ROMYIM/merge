package com.merge.domain;

import java.io.Serializable;

public class LanguageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private int sequence;
    
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
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
        
}
