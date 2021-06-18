package com.example.springbootftpdemo;

import com.example.springbootftpdemo.utils.FtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringbootFtpDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootFtpDemoApplication.class);

    private static FtpUtils ftpUtils;

    public SpringbootFtpDemoApplication (FtpUtils ftpUtils) {
        SpringbootFtpDemoApplication.ftpUtils = ftpUtils;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootFtpDemoApplication.class, args);
        try {
            String path = ftpUtils.upload(args[0]);
            logger.info(path);
        } catch (Exception e) {
            logger.error("upload error, {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
