package merge.aaa.util;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthenticatorUtil {

	private static Logger logger = LoggerFactory.getLogger(AuthenticatorUtil.class);

	public static Authenticator decrypt(String password, String authString, String encrptType){
		try {
			String decStr = "";
			if("3des".equals(encrptType)) {
				ThreeDES des = new ThreeDES();
				des.generateKey(password);
				decStr = des.getDesString(authString);
			}
			else {
				AES aes = new AES();
				decStr = aes.decryptFromHexString(authString, password);;
			}
//			System.out.println("-------------decStr------------" + decStr);
			StringTokenizer token = new StringTokenizer(decStr, "$");
			
			Authenticator auth = new Authenticator();
			auth.setUserId(token.nextToken());
			auth.setStbId(token.nextToken());
			auth.setIp(token.nextToken());
			auth.setMac(token.nextToken());
			auth.setClientType(Integer.parseInt(token.nextToken()));
			
			return auth;
		}catch(Exception err) {
			logger.error("Failed to decrypt. The password maybe wrong. password=" + password + ", authString=" + authString);
			return null;
		}
	}
	
	/* 
	 * 采用3DES算法加密
	 */
	public static String encryptBy3Des(String password, String authString){
		ThreeDES des = new ThreeDES();
		des.generateKey(password);
		return des.getEncString(authString);
		
	}
	
	/*
	 * 采用AES算法加密
	 */
	public static String encryptByAes(String password, String authString){
		AES aes = new AES();
		return aes.encryptToHexString(authString, password);
		
	}
	
	public static void main(String[] args) {
		String password = "123456";
		String authString = "b69b889071a0dde3bcef429294069688ccadda6caef85cfd13eb635dec9e19509a23966eb448e527c7294c0d4dc5802b44f14e7503ce465b49e9778baaa918f719a8edc06bc2733d20ba544562243ee9";

		long start = System.currentTimeMillis();
		System.out.println(AuthenticatorUtil.decrypt(password, authString.toUpperCase(), "3des"));
		long end = System.currentTimeMillis();
		System.out.println("cost=" + (end-start));
		
		password = "123456";
		authString = "208628326401$8973$sunplus001$aabbccdd0011$192.168.1.34$aa:bb:cc:dd:00:11$Reserved$SV";

		start = System.currentTimeMillis();
		String miwen = AuthenticatorUtil.encryptBy3Des(password, authString);
		System.out.println(miwen);
		end = System.currentTimeMillis();
		System.out.println("cost=" + (end-start));
		
	}
}
