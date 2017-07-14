package com.tao.gate;

import java.io.File;

import com.tao.common.xml.XMLUtils;
import com.tao.gate.core.global.reference.SpringContextHolder;
import com.tao.gate.core.server.GateServer;
import com.tao.gate.core.server.GateToAuthConnection;
import com.tao.gate.core.server.GateToLogicConnection;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * Gate网关服务器启动器
 * @author Tao
 *
 */

public class GateStarter {
	
	private static final Logger logger = LoggerFactory.getLogger(GateStarter.class);
	

	//从gate-config.xml配置文件中获取信息
	public static int gateId;				//gateId
	public static String gateServerIp;		//gateServerIp
	public static int gateServerPort;		//gateServerPort
	public static String authServerIp;		//authServerIp
	public static int authServerPort;		//authServerPort
	public static String logicServerIp;	//logicServerIp
	public static int logicServerPort;		//logicServerPort



	public static void main(String[] args) {

	    //启动Gate网关服务器
		GateStarter.configAndStart();
	}
	


	/**
	 * 初始化gate
	 */
	public static void configAndStart() {
		
		//1) 读取xml配置文件
		Element gate = XMLUtils.parseXmlFile("gate/gate-config.xml");
		
		//配置gateId, gateServer, auth, logic
		assert gate != null;
		gateId = Integer.valueOf(gate.attributeValue("id").trim());
		logger.info("gateId= " + gateId);
		
		Element gateServerElement = gate.element("gateServer");
        gateServerIp = gateServerElement.attributeValue("ip").trim();
        gateServerPort = Integer.valueOf(gateServerElement.attributeValue("port").trim());
		logger.info("gateserverIp: {},  gateserverPort: {}", gateServerIp, gateServerPort);
		
		Element authServerElement = gate.element("authServer");
        authServerIp = authServerElement.attributeValue("ip").trim();
        authServerPort = Integer.valueOf(authServerElement.attributeValue("port").trim());
		logger.info("GateAuthConnection authServerIp: {},  authServerPort: {}", authServerIp, authServerPort);
		
		Element logicServerElement = gate.element("logicServer");
        logicServerIp = logicServerElement.attributeValue("ip").trim();
        logicServerPort = Integer.valueOf(logicServerElement.attributeValue("port").trim());
		logger.info("GateLogicConnection logicServerIp: {},  logicServerPort: {}", logicServerIp, logicServerPort);


		//读取Spring配置文件并初始化所有的bean和依赖
        new ClassPathXmlApplicationContext("spring/gate-spring-config.xml");


		//设置GateServer
        GateServer gateServer = (GateServer) SpringContextHolder.getApplicationContext().getBean("gateServer");
        gateServer.setPort(gateServerPort);


        //2) 启动服务
		//启动GateServer服务器
		new Thread(gateServer).start();
		//启动Gate到Auth的一条连接
		new Thread(new GateToAuthConnection(authServerIp, authServerPort)).start();
		//启动Gate到Logic的一条连接
		new Thread(new GateToLogicConnection(logicServerIp, logicServerPort)).start();
	}



}



