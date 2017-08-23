package com.tao.client.client1;

import com.tao.common.heartbeat.CustomHeartbeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by michael on 17-8-22.
 */

/**
 * 客户端心跳handler
 */
public class ClientHeartbeatHandler extends CustomHeartbeatHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);
    
    public ClientHeartbeatHandler(String name) {
        super(name);
    }


    //处理ALL_IDLE事件
    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        //客户端在规定间隔内发生ALL_IDLE, 立即向服务器发送一个Ping
        sendPingMsg(ctx);
    }


    /**
     * 重写channelInActive.当连接断开的时候,该方法会被调用。
     * 在该方法里进行客户端的断线重连。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("TCP连接断开了, 客户端10s后尝试重新连接服务器...");
        //调用doConnect()连接服务器
        Thread.sleep(10*1000);
        //重连
        Client1.doConnect();
        //注意一定要向后边的业务handler传递这个事件,否则后边的handler就被屏蔽了
        ctx.fireChannelInactive();
    }
}
