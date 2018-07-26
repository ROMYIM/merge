package com.merge.config;

import java.net.HttpURLConnection;
import java.net.URL;

public class URLAvailability {
    
    private static URL url;  
    private static int state = -1;  
    
    /**
     * 
     * @MethodName:isConnect
     * @Description:检测当前URL是否可连接或是否有效,最多连接网络 5次,如果 5次都不成功，视为不可用。get请求
     * @author Windy
     * @date 2018年6月15日  上午9:18:06
     */
    public synchronized static boolean isConnect(String urlStr) {  
        int counts = 0;  
        boolean flag = false;
        if (urlStr == null || urlStr.length() <= 0) {     
            return false;                   
        }  
        while (counts < 5) {  
            try {  
                url = new URL(urlStr);  
                HttpURLConnection con = (HttpURLConnection) url.openConnection();    
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
                state = con.getResponseCode();  
                System.out.println("counts: "+counts +", status: "+state);  
                if (state == HttpURLConnection.HTTP_OK) {   //200
                    flag = true;
                }  
                break;  
            }catch (Exception ex) {  
                counts++;   
                System.out.println("URL不可用，连接第 "+counts+" 次， url："+urlStr);  
                //urlStr = null;  
                continue;  
            }  
        }  
        return flag;  
    }  
  
}
