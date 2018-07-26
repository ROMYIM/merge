package com.merge.domain;

import java.io.Serializable;

public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String user;
    private String password;
    private int status;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    

}
