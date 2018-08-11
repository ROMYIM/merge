package com.merge.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.alibaba.fastjson.JSONObject;
import com.merge.config.FtpConfig;

import java.io.IOException;
import java.io.InputStream;

public class FtpFileUtil {
    /**
     * 
     * @MethodName:uploadFile
     * @Description:上传文件至ftp 
     * @author Windy
     * @date 2018年4月10日  上午9:43:40
     */
    public static String uploadFile(String originFileName,InputStream input){
        JSONObject result = new JSONObject();
        String flag = "fail";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(FtpConfig.getFTP_ADDRESS(), FtpConfig.getFTP_PORT());       // 连接FTP服务器
            ftp.login(FtpConfig.getFTP_USERNAME(), FtpConfig.getFTP_PASSWORD());    // 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                result.put("message", flag);
                return JSONObject.toJSONString(result);
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);  //设置文件类型为二进制
            ftp.enterLocalPassiveMode();              //开启pasv模式，不然ftp中无文件
            ftp.makeDirectory(FtpConfig.getFTP_BASEPATH());
            ftp.changeWorkingDirectory(FtpConfig.getFTP_BASEPATH());
            ftp.storeFile(originFileName,input);
            input.close();
            ftp.logout();
            flag = "success";
            result.put("filePath", "http://"+FtpConfig.getFTP_ADDRESS()+"/images/"+originFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        result.put("message", flag);
        return JSONObject.toJSONString(result);
    }

    /**
     * 
     * @MethodName:removeFile
     * @Description:删除指定文件
     * @author Windy
     * @date 2018年4月10日  下午2:13:50
     */
    public static String removeFile(String filePath) {
        String flag = "fail";
        FTPClient ftp = new FTPClient();
        try{
            int reply;
            ftp.connect(FtpConfig.getFTP_ADDRESS(), FtpConfig.getFTP_PORT());    // 连接FTP服务器
            ftp.login(FtpConfig.getFTP_USERNAME(), FtpConfig.getFTP_PASSWORD()); // 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return flag;
            }
            ftp.enterLocalActiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.setControlEncoding("GBK");
            ftp.enterLocalPassiveMode();  
            String file = filePath.substring(filePath.lastIndexOf("/")+1);
            ftp.changeWorkingDirectory(new String(FtpConfig.getFTP_BASEPATH().getBytes("GBK"),FTPClient.DEFAULT_CONTROL_ENCODING));
            // 检验文件是否存在
            InputStream is = ftp.retrieveFileStream(new String(file.getBytes("GBK"),FTPClient.DEFAULT_CONTROL_ENCODING));
//            if(is == null || ftp.getReplyCode() == FTPReply.FILE_UNAVAILABLE){
//                flag = "null";
//            }
            
            if(is != null){
                is.close();
                ftp.completePendingCommand();
                boolean dsuccess = ftp.deleteFile(new String(file.getBytes("gb2312")));  
                if (dsuccess) {  
                    flag = "success"; 
                }  
            }
            ftp.logout();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }
}