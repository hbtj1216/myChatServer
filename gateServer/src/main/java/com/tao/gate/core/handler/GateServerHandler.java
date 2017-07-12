package com.tao.gate.core.handler;


import com.tao.gate.core.domain.ClientConnection;
import com.tao.gate.core.global.dispatcher.ClientMessageDispatcher;
import com.tao.gate.core.global.maps.ClientConnectionMap;
import com.tao.gate.core.global.reference.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 每个客户端连接的I/O处理类。
 * @author Tao
 *
 */
public class GateServerHandler extends SimpleChannelInboundHandler<Message>{
	
	private static final Logger logger = LoggerFactory.getLogger(GateServerHandler.class);

	//持有客户端消息分发器的引用(Spring注入)
	private ClientMessageDispatcher clientMessageDispatcher;
	
	
	public GateServerHandler() {

		//注入依赖clientMessageDispatcher
		this.clientMessageDispatcher = SpringContextHolder.getBean("clientMessageDispatcher");
	}
	
	
	
	/**
	 * 客户端连接建立, 触发
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		logger.info("新的客户端连接到来...");
		//创建一个clientConnection对象
		ClientConnection clientConnection = new ClientConnection(ctx);
		//保存客户端连接到ClientConnectionMap
		ClientConnectionMap.addClientConnection(clientConnection);
	}



	/**
	 * 客户端连接断开, 触发
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		//获得对应的clientConnection对象
		ClientConnection clientConnection = ClientConnectionMap.getClientConnection(ctx);
		//从clientConnectionMap中移除对应的clientConnection
		ClientConnectionMap.removeClientConnection(clientConnection);
	}



	/**
	 * 读客户端消息。
	 * 
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
		
		/**
		 * 注意：
		 * GateServer只负责缓存所有的客户端连接。不对具体的客户端发送过来的消息进行业务处理。
		 * 因此, 当每个客户端对应的GateServerHandler接收到客户端的消息之后，需要通过
		 * GateServer中的消息分发器进行消息的分发。
		 * 每个GateServerHandler持有分发器ClientMessageDispatcher的引用。
		 */
		//获得对应的clientConnection对象
		ClientConnection clientConnection = ClientConnectionMap.getClientConnection(ctx);
		//通过分发器分发消息
		this.clientMessageDispatcher.addMessage(clientConnection, message);
		
	}
	
	
	
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		cause.printStackTrace();
	}


}
