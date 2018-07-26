package merge.aaa.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import merge.aaa.common.Defines;
import merge.aaa.dao.MergeUserDao;
import merge.aaa.domain.MergeUserBean;
import merge.aaa.domain.MergeUserOnlineBean;
import merge.aaa.util.Authenticator;
import merge.aaa.util.AuthenticatorUtil;
import merge.aaa.util.JsonUtil;
import merge.aaa.util.RedisUtils;

@Service
public class AAAService {
	@Resource
	private MergeUserDao mergeUserDao;
	
	@Resource
	private RedisUtils redisUtil;
	
	@Resource
	private JsonUtil jsonUtil;
	
	@Resource
	private RestTemplate restTemplate;
	
	public MergeUserBean getMergeUserByUserId(String userid) {
		return mergeUserDao.getMergeUserByUserId(userid);
	}
	
	/**
	 * @funciton 	获取userToken 组成格式如下：
	 * 				|------------20--------|----------14---------------|---3------------|
	 *				|--------- userinfo----|-----TokenExpireTime-------|---random-------|
	 * 
	 * @param userid
	 * @return 
	 */
	public String createUserToken(String userid){
		StringBuffer str = new StringBuffer(userid);
		for (int i = 0; i < 20-userid.length(); i++) { // userinfo=000...+userid
			str.insert(0, '0');
		}	
		str.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())); // TokenExpireTime
		str.append(new Random().nextInt(900)+100); // random
		return str.toString();
		
	}

	public void setMergeUserOnlineCached(String userid, MergeUserOnlineBean muob) {
		redisUtil.hSet(Defines.IPTVUSERONLINE_TABLE, userid, jsonUtil.toJson(muob));
	}

	public void setUsertokenCached(String usertoken, String userid) {
		redisUtil.hSet(Defines.USERTOKEN_TABLE, usertoken, userid);
		redisUtil.zSet(Defines.USERTOKEN_TIMEOUT_TABLE, usertoken, System.currentTimeMillis() + Defines.tokenexpires *1000);
	}

	public String getUseridCached(String userToken) {
		return redisUtil.hGet(Defines.USERTOKEN_TABLE, userToken);
	}
	
	public Set<String> getAllUsertoken() {
		return redisUtil.hGetKeys(Defines.USERTOKEN_TABLE);
	}
	
	public Set<String> getRangeUsertoken(long start, long end) {
		return redisUtil.zRangeByScore(Defines.USERTOKEN_TIMEOUT_TABLE, start, end);
	}

	public void userLogout(String userid) {
		redisUtil.hDelete(Defines.IPTVUSER_TABLE, userid);
		redisUtil.hDelete(Defines.IPTVUSERONLINE_TABLE, userid);
	}
	
	public void delUsertoken(Set<String> usertokens) {
		for (String usertoken : usertokens) {
			redisUtil.hDelete(Defines.USERTOKEN_TABLE, usertoken);
		}
	}
	
	public void delUsertokenTimeout(long oleTime, long nowTime) {
		redisUtil.zRemoveRangeByScore(Defines.USERTOKEN_TIMEOUT_TABLE, oleTime, nowTime);
	}

	public void updateUsertoken(String oldusertoken, String newusertoken, String userid) {
		redisUtil.hDelete(Defines.USERTOKEN_TABLE, oldusertoken);
		redisUtil.zDelete(Defines.USERTOKEN_TIMEOUT_TABLE, oldusertoken);
		setUsertokenCached(newusertoken, userid);
	}

	public String getEPGIP() {
		return restTemplate.getForEntity("http://zuul/getZuulIp", String.class).getBody();
	}

	public String getAAAIP() {
		return restTemplate.getForEntity("http://feign/getFeignIp", String.class).getBody();
	}
	
	public Authenticator getAuthenticator(String userid, MergeUserBean iptvUser, String Authenticator,
			String encrypt) {
		Authenticator auth = null;
		auth = AuthenticatorUtil.decrypt(userid, Authenticator, encrypt);
		return auth;
	}

}
