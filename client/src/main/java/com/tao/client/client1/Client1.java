package com.tao.client.client1;

import com.tao.protobuf.ParseRegistryMap;
import com.tao.protobuf.codec.ProtoPacketDecoder;
import com.tao.protobuf.codec.ProtoPacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by michael on 17-7-12.
 */
public class Client1 {

    private static final Logger logger = LoggerFactory.getLogger(Client1.class);

    private static final String chatServerIp = "127.0.0.1"; //聊天服务器的ip
    private static final int chatServerPort = 9090;         //聊天服务器的port

    private static final int clientNum = 1;

    private static Channel channel;    //客户端channel
    private static Bootstrap bootstrap;

    public static void main(String[] args) {

        new Client1().startClient();
    }


    /**
     * 启动客户端
     */
    private void startClient() {

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline pipeline = socketChannel.pipeline();

                        //5秒检查一次read write idle
                        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0,
                                                                                        0,
                                                                                        7, TimeUnit.SECONDS));

                        //LengthFieldBasedFrameDecoder
                        pipeline.addLast("LengthFieldBasedFrameDecoder",
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        0,
                                        4,
                                        4,
                                        0));

                        pipeline.addLast("ProtobufPacketDecoder", new ProtoPacketDecoder());	    //解码器

                        pipeline.addLast("ClientHeartbeatHandler", new ClientHeartbeatHandler("Client1"));
                        pipeline.addLast("clientHandler", new ClientHandler());                  //客户端消息处理类

                        pipeline.addLast("ProtobufPacketEncoder", new ProtoPacketEncoder());    //编码器
                    }
                });

        //客户端连接服务器
        doConnect();


    }


    /**
     * 连接远程服务器
     */
    public static void doConnect() {

        if(channel != null && channel.isActive()) {
            return;
        }


        ChannelFuture future = bootstrap.connect(chatServerIp, chatServerPort);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                if(channelFuture.isSuccess()) {

                    //返回成功连接后的channel
                    channel = channelFuture.channel();
                    //注册解析器
                    ParseRegistryMap.initRegistry();
                    logger.info("Client1 connected GateServer Successed...");

                } else {

                    logger.error("Client1 connected GateServer Failed. Try connect again after 10s...");

                    //使用eventLoop的线程池执行定时任务
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            //延迟10s后, 重连
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);

                }
            }
        });
    }

}









