package com.merge.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * session监听SessionListener类 
 *
 */
public class SessionListener implements HttpSessionListener {
    
    public static SessionContext sessionContext=SessionContext.getInstance();  

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        sessionContext.AddSession(httpSessionEvent.getSession());  
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        sessionContext.DelSession(httpSessionEvent.getSession());          
    }

}
