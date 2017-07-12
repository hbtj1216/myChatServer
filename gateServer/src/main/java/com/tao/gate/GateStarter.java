package com.tao.gate;

import java.io.File;

import com.tao.common.xml.XMLUtils;
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
	
	private File configFile = null;	//配置文件
	private File logFile = null; 	//日志文件
	
	//从gate-config.xml配置文件中获取信息
	private int gateId;				//gateId
	private String gateServerIp;	//gateServerIp
	private int gateServerPort;		//gateServerPort
	private String authIp;			//authIp
	private int authPort;			//authPort
	private String logicIp;			//logicIp
	private int logicPort;			//logicPort

    //Spring配置文件
    private ApplicationContext ctx;  //Spring应用上下文



	
	public static void main(String[] args) {

	    //启动Gate网关服务器
		new GateStarter().configAndStart();
	}
	


	/**
	 * 初始化gate
	 */
	private void configAndStart() {
		
		//1) 读取xml配置文件
		Element gate = XMLUtils.parseXmlFile("gate/gate-config.xml");
		
		//配置gateId, gateServer, auth, logic
		this.gateId = Integer.valueOf(gate.attributeValue("id").trim());
		logger.info("gateId= " + gateId);
		
		Element gateServerElement = gate.element("gateServer");
        this.gateServerIp = gateServerElement.attributeValue("ip").trim();
        this.gateServerPort = Integer.valueOf(gateServerElement.attributeValue("port").trim());
		logger.info("gateserverIp: {},  gateserverPort: {}", gateServerIp, gateServerPort);
		
		Element auth = gate.element("auth");
        this.authIp = auth.attributeValue("ip").trim();
        this.authPort = Integer.valueOf(auth.attributeValue("port").trim());
		logger.info("GateAuthConnection authIp: {},  authPort: {}", authIp, authPort);
		
		Element logic = gate.element("logic");
        this.logicIp = logic.attributeValue("ip").trim();
        this.logicPort = Integer.valueOf(logic.attributeValue("port").trim());
		logger.info("GateLogicConnection logicIp: {},  logicPort: {}", logicIp, logicPort);


		//读取Spring配置文件并初始化所有的bean和依赖
        this.ctx = new ClassPathXmlApplicationContext("spring/gate-spring-config.xml");


		//设置GateServer
        GateServer gateServer = (GateServer) this.ctx.getBean("gateServer");
        gateServer.setPort(this.gateServerPort);


        //2) 启动服务
		//启动GateServer服务器
		new Thread(gateServer).start();
		//启动Gate到Auth的一条连接
		new Thread(new GateToAuthConnection(authIp, authPort)).start();
		//启动Gate到Logic的一条连接
		new Thread(new GateToLogicConnection(logicIp, logicPort)).start();
	}



    public ApplicationContext getCtx() {
        return ctx;
    }
}



