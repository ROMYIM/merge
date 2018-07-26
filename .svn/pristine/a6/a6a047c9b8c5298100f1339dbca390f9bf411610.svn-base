/**
 * 
 */
package com.merge.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class AesUserPass {
	
    private final static String Algorithm = "AES"; //定义 加密算法,可用
	private static String KEY_PASSWORD="!@#$%^&*(istarview)";
	private Logger logger = Logger.getLogger(this.getClass());

    /**  
	 * 根据参数生成KEY  
	 * 
	 * @param strKey 密钥字符串  
	 */  
	private SecretKeySpec generateKey(String password) {   
	    try {   
	        KeyGenerator kgen = KeyGenerator.getInstance(Algorithm);  
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());	          
            kgen.init(128, random); 
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, Algorithm); 
            return key;
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return null;
	    }   
	}   

	/**  
	 * 加密String明文输入,String密文输出  
	 *  
	 * @param strMing String明文  
	 * @return String密文 Base64编码  
	 */  
	public String encrypt(String strMing) { 
	    byte[] byteMi = null;   
	    byte[] byteMing = null; 
	    String strMi = "";   
	    try {
	        byteMing = strMing.getBytes();
	        byteMi = this.getEncCode(byteMing, KEY_PASSWORD);   
	        strMi = Base64.encodeBase64String(byteMi);   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	    } finally {   
	        byteMing = null;   
	        byteMi = null;   
	    }   
	    return strMi;   
	}
	  
	/**  
	 * 解密 以String密文输入,String明文输出  
	 *  
	 * @param strMi String密文  Base64格式
	 * @return String明文  
	 */  
	public String decrypt(String strMi) {   
	    byte[] byteMing = null;   
	    byte[] byteMi = null;   
	    String strMing = "";   
	    try {   
	        byteMi = Base64.decodeBase64(strMi);   
	        byteMing = this.getDesCode(byteMi, KEY_PASSWORD);   
	        if(byteMing == null){
	            logger.info("密码解析失败,返回原始密码:"+strMi);
	        	return strMi;
	        }
	        strMing = new String(byteMing);   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	    } finally {   
	        byteMing = null;   
	        byteMi = null;   
	    }   
	    return strMing;   
	}   

	/**  
	 * 加密以byte[]明文输入,byte[]密文输出  
	 *  
	 * @param byteS byte[]明文  
	 * @return byte[]密文  
	 */  
	private byte[] getEncCode(byte[] byteS, String password) {   
	    byte[] byteFina = null;   
	    Cipher cipher;   
	    try {
	        Key key = generateKey(password);
	        cipher = Cipher.getInstance(Algorithm);   
	        cipher.init(Cipher.ENCRYPT_MODE, key);   
	        byteFina = cipher.doFinal(byteS);   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	    } finally {   
	        cipher = null;   
	    }   
	    return byteFina;   
	}   

	/**  
	 * 解密以byte[]密文输入,以byte[]明文输出  
	 *  
	 * @param byteD byte[]密文  
	 * @return byte[]明文  
	 */  
	private byte[] getDesCode(byte[] byteD, String password) {   
	    Cipher cipher;   
	    byte[] byteFina = null;   
	    try {
	      	Key key = generateKey(password);
	        cipher = Cipher.getInstance(Algorithm);   
	        cipher.init(Cipher.DECRYPT_MODE, key);   
	        byteFina = cipher.doFinal(byteD);   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	    } finally {   
	        cipher = null;   
	    }   
	    return byteFina;   
	}   

	public void setKeyPassword(String passwd) {
        KEY_PASSWORD = passwd;
    }

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		String mistr = new AesUserPass().encrypt("star");
		System.out.println("加密后，密文:"+mistr);
		
		String pass = new AesUserPass().decrypt("kzD0JKqqarbw1vE+VkQS/g==");
		System.out.println("解密后，明文:"+pass);
	}*/

}
