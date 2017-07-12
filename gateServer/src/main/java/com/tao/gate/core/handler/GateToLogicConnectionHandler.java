package com.tao.gate.core.handler;

import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ProtobufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * GateServer到LogicServer连接的消息处理类。
 * @author Tao
 *
 */
public class GateToLogicConnectionHandler extends SimpleChannelInboundHandler<Message> {
	
	private static final Logger logger = LoggerFactory.getLogger(GateToLogicConnectionHandler.class);


	//静态域
	/**
	 * GateServer 到 LogicServer 的连接的ctx
	 */
	private static ChannelHandlerContext gateToLogicConnectionContext;


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		this.gateToLogicConnectionContext = ctx;
		logger.info("[GateServer to LogicServer] 连接已经建立成功.");

		//向LogicServer发送Greet协议消息
		sendGreetToLogicServer();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		this.gateToLogicConnectionContext = null;
		logger.info("[GateServer to LogicServer] 连接已经断开.");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

		//LogicServer的处理结果不经过GateServer转发
		
	}


	public static ChannelHandlerContext getGateToLogicConnectionContext() {
		return gateToLogicConnectionContext;
	}



	/**
	 * 向AuthServer发送Greet协议消息
	 */
	private void sendGreetToLogicServer() {

		Internal.Greet.Builder greetB = Internal.Greet.newBuilder();
		greetB.setFrom(Internal.Greet.From.Gate);
		//包装成GTranser
		ByteBuf buf = ProtobufUtils.pack2Server(Internal.DestType.Logic,
				-1, "admin", PtoNum.GREET, greetB.build());
		//发送给Auth
		ChannelFuture future = gateToLogicConnectionContext.writeAndFlush(buf);
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				logger.info("GateServer 向 LogicServer 发送了 Greet 消息.");
			}
		});

	}
}
