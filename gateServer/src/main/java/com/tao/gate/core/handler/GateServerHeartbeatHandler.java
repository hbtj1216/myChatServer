package com.tao.gate.core.handler;

/**
 * Created by michael on 17-8-22.
 */

import com.tao.common.heartbeat.CustomHeartbeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GateServer处理到客户端心跳的Handler.
 */
public class GateServerHeartbeatHandler extends CustomHeartbeatHandler {

    private static final Logger logger = LoggerFactory.getLogger(GateServerHeartbeatHandler.class);

    //心跳失败的计数器
    private int failCounter = 0;


    public GateServerHeartbeatHandler(String name) {
        super(name);
    }

    //处理READER_IDLE事件
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);

        //没有收到客户端的心跳
        failCounter++;
        logger.error("没有收到Client [" + ctx.channel().remoteAddress().toString() + "] 的心跳包, 失败计数器+1, failCount: " + failCounter);

        if(failCounter >= 3) {
            failCounter = 0;
            logger.error("连续丢失三个心跳包,断开Client [" + ctx.channel().remoteAddress().toString() + "] 的连接.");

            //关闭客户端的连接
            ctx.close();
        }


    }
}
