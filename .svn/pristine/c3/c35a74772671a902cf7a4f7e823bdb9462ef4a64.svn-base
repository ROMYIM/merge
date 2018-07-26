package com.merge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class FtpConfig {
    
    private static String FTP_ADDRESS;
    private static int FTP_PORT;
    private static String FTP_USERNAME;
    private static String FTP_PASSWORD;
    private static String FTP_BASEPATH;
    
    public static String getFTP_ADDRESS() {
        return FTP_ADDRESS;
    }

    @Value("${ftp.address}")
    public void setFTP_ADDRESS(String fTP_ADDRESS) {
        FtpConfig.FTP_ADDRESS = fTP_ADDRESS;
    }

    public static int getFTP_PORT() {
        return FTP_PORT;
    }

    @Value("${ftp.port}")
    public void setFTP_PORT(int fTP_PORT) {
        FtpConfig.FTP_PORT = fTP_PORT;
    }

    public static String getFTP_USERNAME() {
        return FTP_USERNAME;
    }

    @Value("${ftp.username}")
    public void setFTP_USERNAME(String fTP_USERNAME) {
        FtpConfig.FTP_USERNAME = fTP_USERNAME;
    }

    public static String getFTP_PASSWORD() {
        return FTP_PASSWORD;
    }

    @Value("${ftp.password}")
    public void setFTP_PASSWORD(String fTP_PASSWORD) {       
        FtpConfig.FTP_PASSWORD = fTP_PASSWORD;
    }

    public static String getFTP_BASEPATH() {
        return FTP_BASEPATH;
    }

    @Value("${ftp.path}")
    public void setFTP_BASEPATH(String fTP_BASEPATH) {
        FtpConfig.FTP_BASEPATH = fTP_BASEPATH;
    }
}
