
package com.example.springbootftpdemo.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h2></h2>
 * @author gitsilence
 * date 2021/6/18
 */
@Component
public class FtpUtils {

    private final Logger logger = LoggerFactory.getLogger(FtpUtils.class);

    @Value("${custom.config.file-server.ip}")
    private String FTP_ADDRESS;

    @Value("${custom.config.file-server.port}")
    private Integer FTP_PORT;

    @Value("${custom.config.file-ftp-user}")
    private String FTP_USERNAME;

    @Value("${custom.config.file-ftp-password}")
    private String FTP_PASSWORD;

    @Value("${custom.config.file-savepath}")
    private String FTP_BASEPATH;

    @Value("${custom.config.enterLocalPassiveMode}")
    private String isEnablePassiveMode;

    //根据当前文件生成 文件夹
    private static String getTimePath() {
        Date now = new Date();

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd/");
        return format.format(now);
    }

    public String upload (String filePath) {
        File file = new File(filePath);
        String originName = file.getName();
        logger.info("originName : {}", originName);
        StringBuilder url = new StringBuilder();
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("utf-8");

        try {
            int reply;
            ftp.enterLocalPassiveMode();
            ftp.setConnectTimeout(120000);
            ftp.setDataTimeout(120000);
            // 连接FTP服务器
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            // 登录
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            reply = ftp.getReplyCode();
            // FTPReply.isPositiveCompletion()
            logger.info("reply: {}", reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.warn("connect failed .... ftp server");
            }
            String timePath = getTimePath();
            String saveDir = FTP_BASEPATH + timePath;
            url.append(saveDir);
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            createDir(ftp, saveDir);
            // originName = System.currentTimeMillis() + originName.substring(originName.lastIndexOf('.'));
            url.append(originName);
            FileInputStream inputStream = FileUtils.openInputStream(file);
            logger.info("parth is : {}", url.toString());

            if ("true".equals(isEnablePassiveMode)) {
                // open passive mode
                ftp.enterLocalPassiveMode();
            }

            boolean b = ftp.storeFile(originName, inputStream);
            logger.info("upload result: {}", (b ? "upload success" : "upload failed "));
            // logout
            ftp.logout();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return url.toString();
    }

    // 创建文件夹，并切换到该文件夹
    // 比如： hello/test
    //最终会切换到test 文件夹返回
    private void createDir(FTPClient client, String path) throws IOException {
        String[] dirs = path.split("/");
        for (String dir : dirs) {
            if (StringUtils.isEmpty(dir)) {
                continue;
            }
            // if not exist, mkdirs
            if (!client.changeWorkingDirectory(dir)) {
                client.makeDirectory(dir);
            }
            client.changeWorkingDirectory(dir);
        }
    }
}
