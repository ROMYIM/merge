package merge.aaa.domain;

import java.io.Serializable;

public class MergeUserOnlineBean implements Serializable  {
	private static final long serialVersionUID = 1L;

	private String userid; // 登录帐号
	private String stbid;
	private String netip; // 公网IP
	private String ip;
	private String mac;
	private int clientType;
	private String logintime; // 登录时间
	private String apptype;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getStbid() {
		return stbid;
	}
	public void setStbid(String stbid) {
		this.stbid = stbid;
	}
	public String getNetip() {
		return netip;
	}
	public void setNetip(String netip) {
		this.netip = netip;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getClientType() {
		return clientType;
	}
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
	public String getLogintime() {
		return logintime;
	}
	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}
	public String getApptype() {
		return apptype;
	}
	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
}
