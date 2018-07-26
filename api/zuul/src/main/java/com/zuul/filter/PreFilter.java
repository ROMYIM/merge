package com.zuul.filter;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.zuul.service.ZuulService;

public class PreFilter extends ZuulFilter{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	ZuulService zuulService;
	
	@Override   
	public boolean shouldFilter() {
		return true;
	}

	@Override   
	public String filterType() {
		return FilterConstants.PRE_TYPE; 
	}

	@Override  
	public int filterOrder() {
		return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
	}
	
	@Override
	public Object run() {
		RequestContext requestContext = getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();
		
		//判断token是否携带
		String usertoken = request.getParameter("usertoken");
		if (usertoken == null) {
			requestContext.setSendZuulResponse(false);//不进行转发
			requestContext.setResponseBody("{\"result\":\"token is null!\"}");  
			requestContext.getResponse().setContentType("text/html;charset=UTF-8");  
			return null;
		}
		
		//判断token是否在缓存中
		String userid = zuulService.getHashKey("USERTOKEN_TABLE",usertoken);
		if (userid == null) {
			requestContext.setSendZuulResponse(false);//不进行转发
			requestContext.setResponseBody("{\"result\":\"token has expired!\"}");  
			requestContext.getResponse().setContentType("text/html;charset=UTF-8");  
			return null;
		}
		
		Map<String, List<String>> map =  requestContext.getRequestQueryParams();
		List<String> list = new ArrayList<>();
		list.add(userid);
		map.put("userid", list);
		requestContext.setRequestQueryParams(map);
		return null;
	}
}
