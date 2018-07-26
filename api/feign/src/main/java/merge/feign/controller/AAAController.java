package merge.feign.controller;

import java.net.InetAddress;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import merge.feign.service.AAAService;

@RestController
public class AAAController {
	
	@Resource
	AAAService aaaService;
	
	@Resource
	private HttpServletRequest request;
	
	@RequestMapping(value = "/aaa/login")
	public String login(@RequestParam(value = "userid") String userid,
			@RequestParam(value = "Authenticator") String Authenticator){
		String clientIP = request.getRemoteHost();
		System.out.println("clientIP:" + clientIP);
		return aaaService.login(userid, Authenticator, clientIP);
	}
	
	@RequestMapping(value = "/aaa/updateUserToken")
	public String updateUserToken(@RequestParam(value = "oldusertoken") String oldusertoken){
		return aaaService.updateUserToken(oldusertoken);
	}
	
	@RequestMapping("getFeignIp")
	public String getFeignIp() {
		String feignIp = "";
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			byte[] ip = inetAddress.getAddress();
			for(int i = 0; i < ip.length; i++) {
				int ips = (ip[i] >= 0) ? ip[i] : (ip[i]+256);
				feignIp = feignIp+ips+".";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		feignIp = feignIp.substring(0,feignIp.length()-1);
		feignIp = "http://"+feignIp;
		return feignIp;
	}
	
}
