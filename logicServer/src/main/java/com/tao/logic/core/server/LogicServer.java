package com.tao.logic.core.server;

/**
 * Created by tao on 2017/7/6.
 */

import com.tao.logic.core.component.LogicCenter;
import com.tao.logic.core.global.HandlerManager;
import com.tao.logic.core.handler.LogicServerHandler;
import com.tao.protobuf.ParseRegistryMap;
import com.tao.protobuf.codec.ProtoPacketDecoder;
import com.tao.protobuf.codec.ProtoPacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * LogicServer逻辑服务器.
 * 负责处理用户聊天信息.
 */
public final class LogicServer implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(LogicServer.class);
    
    private int port;   //logicServer监听的端口

    private LogicCenter logicCenter;    //逻辑处理中心




    @Override
    public void run() {

        startLogicServer(port);
    }


    private void startLogicServer(int port) {

        /**
         * logicServer作为服务器, 等待GateServer连接.
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast("ProtobufPacketDecoder", new ProtoPacketDecoder());	//解码器
                        pipeline.addLast("ProtobufPacketEncoder", new ProtoPacketEncoder());	//编码器
                        pipeline.addLast("LogicServerHandler", new LogicServerHandler(logicCenter));
                    }
                });


        //设置TCP参数
        setConnectionOption(serverBootstrap);


        //绑定端口, 启动连接
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port));
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                if(future.isSuccess()) {
                    //初始化
                    ParseRegistryMap.initRegistry();    //初始化协议的解析器
                    HandlerManager.initMsgHandlers();   //初始化消息的处理器
                    logger.info("[LogicServer] started successed, waiting for other server connect...");
                } else {
                    logger.info("[LogicServer] started failed.");
                }
            }
        });
    }



    private void setConnectionOption(ServerBootstrap serverBootstrap) {

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        serverBootstrap.childOption(ChannelOption.SO_LINGER, 0);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true); //可重用端口(调试用)
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //心跳机制暂时使用TCP选项，之后再自己实现
    }



    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public LogicCenter getLogicCenter() {
        return logicCenter;
    }

    public void setLogicCenter(LogicCenter logicCenter) {
        this.logicCenter = logicCenter;
    }
}












