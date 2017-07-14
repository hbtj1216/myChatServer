package com.tao.logic;

import com.tao.common.xml.XMLUtils;
import com.tao.logic.core.global.reference.SpringContextHolder;
import com.tao.logic.core.server.LogicServer;
import com.tao.thirdparty.redis.RedisPoolManager;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by michael on 17-7-1.
 */

/**
 * Logic服务器的 启动器.
 */
public class LogicStarter {

    private final static Logger logger = LoggerFactory.getLogger(LogicStarter.class);


    public static int logicServerPort;  //logicServer端口号
    public static  int workerNum;       //工作线程的数量

    public static String redisIp;
    public static int redisPort;
    public static RedisPoolManager redisPoolManager;  //redis的连接池管理器


    public static void main(String[] args) {

        LogicStarter.configAndStart();
    }


    /**
     * 配置和启动服务.
     */
    public static void configAndStart() {

        //读取配置文件
        Element logic = XMLUtils.parseXmlFile("logic/logic-config.xml");

        assert logic != null;
        Element logicServerElement = logic.element("logicServer");
        logicServerPort = Integer.valueOf(logicServerElement.attributeValue("port").trim());
        workerNum = Integer.valueOf(logicServerElement.attributeValue("workerNum").trim());
        logger.info("logicServerPort: {}, workerNum: {}", logicServerPort, workerNum);

        Element redisElement = logic.element("redis");
        redisIp = redisElement.attributeValue("ip").trim();
        redisPort = Integer.valueOf(redisElement.attributeValue("port").trim());
        logger.info("redisIp: {}, redisPort: {}", redisIp, redisPort);

        redisPoolManager = new RedisPoolManager();
        //redisPoolManager.REDIS_SERVER_IP = redisIp;
        //redisPoolManager.REDIS_SERVER_PORT = redisPort;
        //redisPoolManager.returnJedis(redisPoolManager.getJedis());

        logger.info("Redis init successed.");


        //读取spring
        new ClassPathXmlApplicationContext("spring/logic-spring-config.xml");

        //配置logicServer
        LogicServer logicServer = (LogicServer) SpringContextHolder.getApplicationContext().getBean("logicServer");
        logicServer.setPort(logicServerPort);


        //启动LogicServer
        new Thread(logicServer).start();

    }

}













