package com.merge.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.merge.domain.UserBean;
import com.merge.util.Const;

@Component
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter{

	public Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//判断用户是否登录
		HttpSession session = request.getSession();
		UserBean user = (UserBean)session.getAttribute(Const.SESSION_USER);
//		 String urlString = request.getRequestURI();
//		 //模拟登录页,这个地方要做判断不然会无限循环重定向，即执行过滤器、拦截器很多遍
//	     if(urlString.endsWith("index")){
//	            return true;
//	     }
		if(request.getServletPath().equals("/") || request.getServletPath().equals("login")){
		    return true;
		}
	    if(user == null){
	         //跳转登录页
	        // response.sendRedirect("/");
	        return true;
	    } else {
	        return true;
	    }
	}
	
}
