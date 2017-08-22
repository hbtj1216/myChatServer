package com.tao.gate.core.handler;

/**
 * Created by michael on 17-8-22.
 */

import com.tao.common.heartbeat.CustomHeartbeatHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * GateServer处理到客户端心跳的Handler.
 */
public class GateServerHeartbeatHandler extends CustomHeartbeatHandler {

    public GateServerHeartbeatHandler(String name) {
        super(name);
    }

    //处理READER_IDLE事件
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        System.err.println("客户端 " + ctx.channel().remoteAddress().toString() + " 写超时, 关闭该客户端连接!!!");
        //主动关闭该客户端连接
        ctx.close();
    }
}
