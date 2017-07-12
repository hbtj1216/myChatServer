package com.tao.auth.core.handler.messageHandler;

/**
 * Created by michael on 17-7-4.
 */

import com.google.protobuf.Message;
import com.tao.auth.core.component.Worker;
import com.tao.auth.core.handler.AuthServerHandler;
import com.tao.auth.core.handler.MsgHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理Greet消息的Handler.
 */
public class GreetHandler extends MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(GreetHandler.class);


    public GreetHandler(String userId, long netId, Message message, ChannelHandlerContext ctx) {
        super(userId, netId, message, ctx);
    }



    @Override
    public void excute(Worker worker) throws Exception {
        //连接成功, 设置gateConnCtx
        AuthServerHandler.setGateConnCtx(this.ctx);
        logger.info("[Gate-Auth] connection is established");
    }
}







