package com.tao.gate.core.handler;

import com.tao.gate.core.global.maps.ClientConnectionMap;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ServerProtoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Gate到Auth的连接的消息处理类。
 * Gate是客户端, Auth是服务器。
 * @author Tao
 *
 */
public class GateToAuthConnectionHandler extends SimpleChannelInboundHandler<Message> {
	
	private static final Logger logger = LoggerFactory.getLogger(GateToAuthConnectionHandler.class);


	//静态域
	/**
	 * Gate到Auth的连接的ctx
	 */
	private static ChannelHandlerContext gateToAuthConnectionContext;




	/**
	 * 当连接 被建立, 并且准备通信时被调用。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		//连接创建成功, 保存ctx到静态域中,便于外部类使用
		gateToAuthConnectionContext = ctx;
		logger.info("[GateServer to AuthServer] 连接已经建立成功.");

		//向AuthServer发送Greet协议消息
		sendGreetToAuthServer();
	}



	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		//连接断开, 注销
		gateToAuthConnectionContext = null;
		logger.info("[GateServer to AuthServer] 连接已经断开.");
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		cause.printStackTrace();
	}



	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

	    //收到的消息为GTransfer
		//收到AuthServer发回的消息
		Internal.GTransfer gtf = (Internal.GTransfer) msg;
		String userId = gtf.getUserId();    //userId
		Long netId = gtf.getNetId();        //netId
		//获得sResponse
		Message message = ParseMap.getMessage(gtf.getPtoNum(), gtf.getMsg().toByteArray());
		logger.info("[GateServer] 收到 [AuthServer] 发回的消息 : [{}]", message.getClass().getSimpleName());

		//GateServer从AuthServer收到的是SResponse消息体(登录或者注册的返回信息)
		//根据code进行判断
        Auth.SResponse sResponse = (Auth.SResponse) message;
        int code = sResponse.getCode();
        if(code == Common.LOGIN_SUCCESS) {
            //登录成功, 将登录的用户添加到GateServer的缓存中
            ClientConnectionMap.registerLoginUser(userId, netId);
        }
		//将消息打包
		ByteBuf buf = ServerProtoUtils.pack2Client(message);
		//找到对应的客户端连接的ctx, 将消息发送回客户端
		ClientConnectionMap.getClientConnection(gtf.getNetId()).getCtx().writeAndFlush(buf);
	}
	
	/**
	 * 向AuthServer发送Greet协议消息
	 */
	private void sendGreetToAuthServer() {
		
		Internal.Greet.Builder greetB = Internal.Greet.newBuilder();
		greetB.setFrom(Internal.Greet.From.Gate);
		//包装成GTranser
		ByteBuf buf = ServerProtoUtils.pack2Server(Internal.DestType.Auth,
						-1, "admin", PtoNum.GREET, greetB.build());

		//发送给AuthServer
		ChannelFuture future = gateToAuthConnectionContext.writeAndFlush(buf);
		future.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				
				logger.info("GateServer 向 AuthServer 发送了 Greet 消息.");
			}
		});
		
	}


	/**
	 * 获得Gate到Auth的连接的ctx
	 * @return
	 */
	public static ChannelHandlerContext getGateToAuthConnectionContext() {
		return gateToAuthConnectionContext;
	}
}












