package merge.aaa.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import merge.aaa.common.Defines;
import merge.aaa.domain.MergeUserBean;
import merge.aaa.domain.MergeUserOnlineBean;
import merge.aaa.service.AAAService;
import merge.aaa.util.Authenticator;
import net.sf.json.JSONObject;

@RestController
public class AAAController {

	@Resource
	private HttpServletRequest request;

	@Resource
	private AAAService aaaService;

	@RequestMapping(value = "login")
	public String login(@RequestParam(value = "userid") String userid,
			@RequestParam(value = "Authenticator") String Authenticator,
			@RequestParam(value = "clientIP", required = false) String clientIP) {
		MergeUserBean mergeUser = null;
		JSONObject resultJS = new JSONObject();
		String encrypt = "3des";
		int returnCode = 0;
		String description = "Login Success";
		String usertoken = "";

		MergeUserBean mub = aaaService.getMergeUserByUserId(userid);
		int status = 0;
		if (null != mub) {
			status = mub.getStatus();
			Authenticator auth = aaaService.getAuthenticator(userid, mergeUser, Authenticator, encrypt);
			if (null != auth) {
				try {
					usertoken = aaaService.createUserToken(userid);

					/**
					 * 记录终端在线状态 begin
					 ************************************/
					if (status != 0) {
						MergeUserOnlineBean muob = new MergeUserOnlineBean();
						muob.setIp(auth.getIp());
						muob.setMac(auth.getMac());
						muob.setNetip(clientIP);
						muob.setStbid(auth.getStbId());
						muob.setClientType(auth.getClientType());
						muob.setUserid(userid);
						aaaService.setMergeUserOnlineCached(userid, muob);
						aaaService.setUsertokenCached(usertoken, userid);
					}
				} catch (Exception e) {
					returnCode = 101;// 更新用户Token失败
					description = "Login failure";
					e.printStackTrace();
				}
			} else {
				returnCode = 102;// 登录失败
				description = "Invalid password";
			}
		} else {
			returnCode = 103;// 用户不存在
			description = "Login failure, " + userid + " inexistence.";
		}
		resultJS.put("returnCode", returnCode);
		resultJS.put("description", description);

		if (returnCode == 0) { // 正常
			String aaaPath = aaaService.getAAAIP() + ":9160/aaa";
			String epgPath = aaaService.getEPGIP();
			resultJS.put("userid", mub.getUserid());
			resultJS.put("status", mub.getStatus());
			resultJS.put("usertoken", usertoken);
			resultJS.put("effectiveTime", mub.getEffectivetime());
			resultJS.put("categoryIndex", String.format("%s/category/index?cid=%d", epgPath, mub.getCategory()));

			resultJS.put("TokenUpdateUrl", aaaPath + "/updateUserToken");
			resultJS.put("TokenExpireTime", String.valueOf(Defines.tokenexpires));// USERTOKEN更新周期（单位:秒）
		} else {
			resultJS.put("state", "error");
		}

		return resultJS.toString();
	}
	
	@RequestMapping("getCategoryIdByUserid")
	public Integer getCategoryIdByUserid(@RequestParam(value = "userid" , required = false) String userid) {
		MergeUserBean mub = aaaService.getMergeUserByUserId(userid);
		return mub.getCategory();
	}
	
	@RequestMapping(value = "updateUserToken")
	public String updateUserToken(@RequestParam(value = "oldusertoken") String oldusertoken) {
		String userid = aaaService.getUseridCached(oldusertoken);
		JSONObject resultJS = new JSONObject();
		String returnCode = "0";
		String description = "Update UserToken success";
		if (userid != null) {
			String newusertoken = aaaService.createUserToken(userid);
			try {
				aaaService.updateUsertoken(oldusertoken, newusertoken, userid);
				resultJS.put("newUserToken", newusertoken);
				resultJS.put("tokenExpiredTime", Defines.tokenexpires);
				resultJS.put("userid", userid);
			} catch (Exception e) {
				returnCode = "110";
				description = "Update UserToken errors";
			}
		} else {
			returnCode = "111";
			description = "Update UserToken errors,UserToken=" + oldusertoken + " inexistence";
		}
		resultJS.put("returnCode", returnCode);
		resultJS.put("description", description);
		return resultJS.toString();
	}
}
