package com.merge.config;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * SessionContex类(使用单例模式) 
 *
 */
public class SessionContext {

    private static SessionContext instance;  
    private HashMap<String,HttpSession> sessionMap;  
   
    private SessionContext() {  
        sessionMap = new HashMap<String,HttpSession>();  
    }  
  
    public static SessionContext getInstance() {  
        if (instance == null) {  
            instance = new SessionContext();  
        }  
        return instance;  
    }  
  
    public synchronized void AddSession(HttpSession session) {  
        if (session != null) {  
            sessionMap.put(session.getId(), session);  
        }  
    }  
  
    public synchronized void DelSession(HttpSession session) {  
        if (session != null) {  
            sessionMap.remove(session.getId());  
            if(session.getAttribute("userid")!=null){  
                sessionMap.remove(session.getAttribute("userid").toString());  
                //session.invalidate();   
            }  
        }  
    }  
  
    public synchronized HttpSession getSession(String session_id) {  
        if (session_id == null) return null;  
        return (HttpSession) sessionMap.get(session_id);  
    }  
  
    public HashMap<String, HttpSession> getSessionMap() {  
        return sessionMap;  
    }  
  
    public void setMymap(HashMap<String, HttpSession> sessionMap) {  
        this.sessionMap = sessionMap;  
    }  
  
}
