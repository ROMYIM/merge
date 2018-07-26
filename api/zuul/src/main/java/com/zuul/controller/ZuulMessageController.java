package com.zuul.controller;

import java.net.InetAddress;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZuulMessageController {
	
	@RequestMapping("getZuulIp")
	public String getZuulIp() {
		String zuulIp = "";
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			byte[] ip = inetAddress.getAddress();
			for(int i = 0; i < ip.length; i++) {
				int ips = (ip[i] >= 0) ? ip[i] : (ip[i]+256);
				zuulIp = zuulIp+ips+".";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		zuulIp = zuulIp.substring(0,zuulIp.length()-1);
		zuulIp = "http://"+zuulIp + ":9190";
		return zuulIp;
	}
}
