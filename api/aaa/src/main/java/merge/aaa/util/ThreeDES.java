package merge.aaa.util;


import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
  
/**  
 * 使用3DES加密与解密,可对byte[],String类型进行加密与解密 密文可使用String,byte[]存储.  
 *  
 */  
public class ThreeDES {
	private static final String Algorithm = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish
	
    private Key key;        //密钥   
  
    /**  
     * 根据参数生成KEY  
     *  
     * @param strKey 密钥字符串  
     */  
    public void generateKey(String strKey) {   
        try {   

        	StringBuffer sb = new StringBuffer(strKey);
        	while(sb.length()<24) { // 补齐24位。右边补齐0{
        		sb.append("0");
        	}
        	
            SecretKey deskey = new SecretKeySpec(sb.toString().getBytes(), Algorithm);
            key = deskey;

        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
  
    /**  
     * 加密String明文输入,String密文输出  
     *  
     * @param strMing String明文  
     * @return String密文  
     */  
    public String getEncString(String strMing) {   
        byte[] byteMi = null;   
        byte[] byteMing = null;   
        String strMi = "";   
        try {
        	byteMing = strMing.getBytes();
            byteMi = this.getEncCode(byteMing);   
            strMi = ThreeDES.byteArr2HexStr(byteMi);   
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
     * @param strMi String密文  
     * @return String明文  
     */  
    public String getDesString(String strMi) {   
        byte[] byteMing = null;   
        byte[] byteMi = null;   
        String strMing = "";   
        try {   
            byteMi = ThreeDES.hexStr2ByteArr(strMi);   
            byteMing = this.getDesCode(byteMi);   
            strMing = new String(byteMing);   
        } catch (Exception e) { 
        	System.out.println(e.getMessage());
//            e.printStackTrace();   
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
    private byte[] getEncCode(byte[] byteS) {   
        byte[] byteFina = null;   
        Cipher cipher;   
        try {   
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
    private byte[] getDesCode(byte[] byteD) {   
        Cipher cipher;   
        byte[] byteFina = null;   
        try {   
            cipher = Cipher.getInstance(Algorithm);   
            cipher.init(Cipher.DECRYPT_MODE, key);   
            byteFina = cipher.doFinal(byteD);   
        } catch (Exception e) { 
        	System.out.println(e.getMessage());
//            e.printStackTrace();   
        } finally {   
            cipher = null;   
        }   
        return byteFina;   
    }   
  
    //16进制字符串转数组   
    private static byte[] hexStr2ByteArr(String strIn) throws Exception {   
        byte[] arrB = strIn.getBytes();   
        int iLen = arrB.length;   
  
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2   
        byte[] arrOut = new byte[iLen / 2];   
        for (int i = 0; i < iLen; i = i + 2) {   
            String strTmp = new String(arrB, i, 2);   
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);   
        }   
        return arrOut;   
    }   
       
    //数组转16进制字符串   
    public static String byteArr2HexStr(byte[] arrB) throws Exception {    
        int iLen = arrB.length;   
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍   
        StringBuffer sb = new StringBuffer(iLen * 2);   
        for (int i = 0; i < iLen; i++) {   
            int intTmp = arrB[i];   
            // 把负数转换为正数   
            while (intTmp < 0) {   
                intTmp = intTmp + 256;   
            }   
            // 小于0F的数需要在前面补0   
            if (intTmp < 16) {   
                sb.append("0");   
            }   
            sb.append(Integer.toString(intTmp, 16));   
        }   
        // 最大128位   
        String result = sb.toString();   
//      if(result.length()>128){   
//          result = result.substring(0,result.length()-1);   
//      }   
        return result;   
    }   
    public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public static void main(String[] args) throws Exception {   
    }   
}  
