package com.tao.gate.core.server;

import com.tao.gate.core.handler.GateToAuthConnectionHandler;
import com.tao.protobuf.codec.ProtoPacketDecoder;
import com.tao.protobuf.codec.ProtoPacketEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Gate网关服务器到Auth认证服务器的连接。
 * @author Tao
 *
 */
public class GateToAuthConnection implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(GateToAuthConnection.class);
	
	private String authIp;	//authServer的地址
	private int authPort;	//authServer的端口号
	
	
	public GateToAuthConnection(String authIp, int authPort) {

		this.authIp = authIp;
		this.authPort = authPort;
	}


	@Override
	public void run() {

		this.buildGateToAuthConnection();
	}
	
	
	/**
	 * 建立Gate网关服务器到Auth认证服务器的连接.
	 */
	public void buildGateToAuthConnection() {
		/**
		 * 使用Netty框架建立一条Gate网关服务器到Auth认证服务器的连接。
		 * 在这里：
		 * Gate是 客户端角色, Auth是 服务器角色。
		 */
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		
		bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					
					ChannelPipeline pipeline = ch.pipeline();

                    //LengthFieldBasedFrameDecoder
                    pipeline.addLast("LengthFieldBasedFrameDecoder",
                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                    0,
                                    4,
                                    4,
                                    0));
					pipeline.addLast("ProtobufPacketDecoder", new ProtoPacketDecoder());	//解码器
                    //消息处理器
                    pipeline.addLast("GateToAuthConnectionHandler", new GateToAuthConnectionHandler());
                    pipeline.addLast("ProtobufPacketEncoder", new ProtoPacketEncoder());	//编码器
				}
			});
		
		//连接Auth服务器
		ChannelFuture future = bootstrap.connect(this.authIp, this.authPort);
		future.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				if(future.isSuccess()) {
					logger.info("[GateToAuthConnection] 连接建立成功！");
					
				}
				else {
					logger.error("[GateToAuthConnection] 连接建立失败！");
				}	
			}
		});
	}



	
}










