package merge.aaa.util;

public class Authenticator {
	private String userId;
	private String stbId;
	private String ip;
	private String mac;
	private int clientType;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStbId() {
		return stbId;
	}
	public void setStbId(String stbId) {
		this.stbId = stbId;
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
	public String toString() {
		return "userId=" + userId + ", stbId=" + stbId + ", ip=" + ip + ", mac=" + mac + ", clientType=" + clientType;
	}

}
