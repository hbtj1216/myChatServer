package com.tao.gate.core.server;

import java.net.InetSocketAddress;

import com.tao.gate.core.global.dispatcher.ClientMessageDispatcher;
import com.tao.gate.core.handler.GateServerHandler;
import com.tao.gate.core.handler.GateServerHeartbeatHandler;
import com.tao.protobuf.ParseRegistryMap;
import com.tao.protobuf.codec.ProtoPacketDecoder;
import com.tao.protobuf.codec.ProtoPacketEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * Gate网关服务器。
 * @author Tao
 *
 */
public class GateServer implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(GateServer.class);
	
	private int port;	//gateServer监听的端口
	private ClientMessageDispatcher clientMessageDispatcher;	//客户端消息分发器(Spring注入)


	@Override
	public void run() {

		this.startGateServer();
	}


	/**
	 * 启动消息分发器组件
	 */
	private void startDispatcher() {

		new Thread(clientMessageDispatcher).start();
	}


	/**
	 * 启动Gate网关服务器。
	 */
	private void startGateServer() {
			
			//1) 配置连接
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						
						ChannelPipeline pipeline = ch.pipeline();


                        //服务器每隔10秒检测一次READER_IDLE
                        pipeline.addLast("IdleStateHandler", new IdleStateHandler(14,
                                                                                        0,
                                                                                        0));

						//LengthFieldBasedFrameDecoder
						pipeline.addLast("LengthFieldBasedFrameDecoder",
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                                                0,
                                                                4,
                                                                4,
                                                                0));
                        //解码器
						pipeline.addLast("ProtobufPacketDecoder", new ProtoPacketDecoder());
                        //gateServer心跳处理程序
                        pipeline.addLast("GateServerHeartbeatHandler", new GateServerHeartbeatHandler("gateServer"));
                        //消息处理器
						pipeline.addLast("GateServerHandler", new GateServerHandler());
                        //编码器
                        pipeline.addLast("ProtobufPacketEncoder", new ProtoPacketEncoder());

					}
				});
			
			//2) 设置连接的选项
			this.setConnectionOption(serverBootstrap);	
			
			//3) 绑定端口, 建立服务器连接
			ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(this.port));
			future.addListener(new ChannelFutureListener() {
				
				/**
				 * 配置绑定端口操作的监听器
				 */
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					
					//服务器绑定端口建立监听操作成功返回, 则进行一些初始化操作。
					if(future.isSuccess()) {
						//初始化操作
						//1) 初始化protobuf消息的解析函数
						ParseRegistryMap.initRegistry();
						//2) 开启单独的线程, 启动dispatcher分发器
						startDispatcher();
						
	                    logger.info("[GateServer] 成功启动, 初始化工作完成, 正在等待客户端的连接...");
					}
					else {
						logger.error("[GateServer] 启动失败, 请重试!");
					}
					
				}
			});
		
	}
	
	
	/**
	 * 设置连接的选项。
	 * @param serverBootstrap
	 */
	private void setConnectionOption(ServerBootstrap serverBootstrap) {
		
		serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

		serverBootstrap.childOption(ChannelOption.SO_LINGER, 0);
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true); //调试用
		//serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //心跳机制暂时使用TCP选项，之后再自己实现
	}


	public void setPort(int port) {
		this.port = port;
	}

	public void setClientMessageDispatcher(ClientMessageDispatcher clientMessageDispatcher) {
		this.clientMessageDispatcher = clientMessageDispatcher;
	}
}















