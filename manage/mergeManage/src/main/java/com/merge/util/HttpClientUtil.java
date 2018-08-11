package com.merge.util;

import java.io.IOException;

import com.merge.config.SSLClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
    /**
     * 
     * @MethodName:doHttpsPost
     * @Description:https post请求
     * @author Windy
     * @date 2018年3月21日  下午6:08:04
     */
    public static String doHttpsPost(String url,String jsonstr,String charset,String Authorization){
        HttpClient httpClient = null; 
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            if (!Authorization.equals("")){
                httpPost.setHeader("Authorization", Authorization);
            }else {
                httpPost.addHeader("Content-Type", "application/json");
            }
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setConnectionRequestTimeout(60000).setSocketTimeout(60000).build();
            httpPost.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * @MethodName:doHttpsGet
     * @Description:https get请求
     * @author Windy
     * @date 2018年3月23日  上午8:51:49
     */
    public static String doHttpsGet(String url,String charset, String Authorization){  
        if(null == charset){  
            charset = "utf-8";  
        }  
        HttpClient httpClient = null;  
        HttpGet httpGet= null;  
        String result = null;           
        try {  
            httpClient = new SSLClient();  
            httpGet = new HttpGet(url); 
            if (!Authorization.equals("")){
                httpGet.setHeader("Authorization", Authorization);
                httpGet.addHeader("Accept", "application/json");
                httpGet.addHeader("Content-Type", "application/json");
            } else {
                httpGet.addHeader("Content-Type", "application/json");
            } 
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(1000).setSocketTimeout(5000).build();
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }         
        return result;  
    }  
    
    /**
     * 
     * @MethodName:sendHttpGet
     * @Description:发送HttpGet请求
     * @author Windy
     * @date 2018年3月21日  下午6:08:33
     */
    public static String sendHttpGet(String url) {   
        CloseableHttpClient httpclient = HttpClients.createDefault();  //1.获得一个httpclient对象       
        HttpGet httpget = new HttpGet(url);  //2.生成一个get请求
        CloseableHttpResponse response = null;
        String result = null;
        try {     
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(600000).setConnectionRequestTimeout(600000).setSocketTimeout(600000).build();
            httpget.setConfig(requestConfig);
            response = httpclient.execute(httpget);    //3.执行get请求并返回结果      
            HttpEntity entity = response.getEntity();  //4.处理结果，这里将结果返回为字符串
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}