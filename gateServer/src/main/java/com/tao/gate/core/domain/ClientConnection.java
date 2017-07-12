package com.tao.gate.core.domain;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接的封装类。
 * @author Tao
 *
 */
public class ClientConnection {

	private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

	public static AttributeKey<Long> NETID = AttributeKey.valueOf("netId");
	//网络id生成器
	private static final AtomicLong netIdGenerator = new AtomicLong(0);
	
	
	private String userId;	//用户id
	private long netId;		//全局唯一的网络id
	private ChannelHandlerContext ctx;	//上下文
	
	
	public ClientConnection(ChannelHandlerContext ctx) {
		
		//为当前对象生成netId
		this.netId = ClientConnection.netIdGenerator.incrementAndGet();
		this.ctx = ctx;
		//向channel中添加属性NETID = netId
		this.ctx.channel().attr(ClientConnection.NETID).set(this.netId);
		logger.info("新接入的客户端netId = {}", this.netId);
	}
	
	
	public String getUserId() {
		return userId;
	}
	
	public long getNetId() {
		return netId;
	}
	
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	
	public ClientConnection setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public ClientConnection setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		return this;
	}


	@Override
	public String toString() {
		return "ClientConnection [userId=" + userId + ", netId=" + netId + ", ctx=" + ctx + "]";
	}
	
	
}






