package com.tao.client.client2;

import com.tao.protobuf.ParseRegistryMap;
import com.tao.protobuf.codec.ProtoPacketDecoder;
import com.tao.protobuf.codec.ProtoPacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by michael on 17-7-12.
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static final String chatServerIp = "127.0.0.1"; //聊天服务器的ip
    private static final int chatServerPort = 9090;         //聊天服务器的port

    private static final int clientNum = 1;



    public static void main(String[] args) {

        new Client().startClient();
    }


    /**
     * 启动客户端
     */
    private void startClient() {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast("ProtobufPacketDecoder", new ProtoPacketDecoder());	    //解码器
                        pipeline.addLast("ProtobufPacketEncoder", new ProtoPacketEncoder());    //编码器
                        pipeline.addLast("clientHandler", new ClientHandler());                     //客户端消息处理类
                    }
                });


        //启动多个客户端
        for(int i = 1; i <= clientNum; i++) {
            startConnection(bootstrap, i);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 启动连接(第i个)
     * @param bootstrap
     * @param i
     */
    private void startConnection(Bootstrap bootstrap, int i) {

        ChannelFuture future = bootstrap.connect(chatServerIp, chatServerPort);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                if(channelFuture.isSuccess()) {
                    ParseRegistryMap.initRegistry();
                    logger.info("Client[{}] connected Gate Successed...", i);
                } else {
                    logger.error("Client[{}] connected Gate Failed", i);
                }
            }
        });
    }

}









