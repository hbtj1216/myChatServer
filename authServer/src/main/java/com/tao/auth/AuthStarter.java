package com.tao.auth;


import com.tao.auth.core.global.reference.SpringContextHolder;
import com.tao.auth.core.server.AuthServer;
import com.tao.common.xml.XMLUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by michael on 17-7-1.
 */

/**
 * Auth认证服务器的启动器.
 */
public class AuthStarter {

    private static final Logger logger = LoggerFactory.getLogger(AuthStarter.class);


    public static int authServerPort; //authServer监听的端口
    public static int workerNum;      //worker的数量

    public static String redisIp;
    public static int redisPort;


    public static void main(String[] args) {

        AuthStarter.configAndStart();
    }



    public static void configAndStart() {

        //读取xml配置文件，配置参数
        Element auth = XMLUtils.parseXmlFile("auth/auth-config.xml");

        //配置authServer、redis、logic
        Element authServerElement = auth.element("authServer");
        authServerPort = Integer.valueOf(authServerElement.attributeValue("port").trim());
        workerNum = Integer.valueOf(authServerElement.attributeValue("workerNum").trim());
        logger.info("authServerPort : {}, workerNum : {}", authServerPort, workerNum);

        Element redisElement = auth.element("redis");
        redisIp = redisElement.attributeValue("ip").trim();
        redisPort = Integer.valueOf(redisElement.attributeValue("port").trim());
        logger.info("redisIp : {}, redisPort : {}", redisIp, redisPort);



        //读取spring
        new ClassPathXmlApplicationContext("spring/auth-spring-config.xml");

        //配置authServer
        AuthServer authServer = (AuthServer) SpringContextHolder.getApplicationContext().getBean("authServer");
        authServer.setPort(authServerPort);


        //启动AuthServer服务器
        new Thread(authServer).start();

    }



}




















