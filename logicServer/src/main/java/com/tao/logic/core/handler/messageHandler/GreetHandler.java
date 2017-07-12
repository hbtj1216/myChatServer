package com.tao.logic.core.handler.messageHandler;

/**
 * Created by michael on 17-7-4.
 */

import com.google.protobuf.Message;

import com.tao.logic.core.component.Worker;
import com.tao.logic.core.handler.LogicServerHandler;
import com.tao.logic.core.handler.MsgHandler;
import com.tao.protobuf.message.internal.Internal;
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

        //首先获得Greet对象
        Internal.Greet greet = (Internal.Greet) msg;
        Internal.Greet.From from = greet.getFrom();

        //判断Greet消息是从GateServer还是AuthServer发来的
        if(from == Internal.Greet.From.Gate) {
            //从Gate发来的
            LogicServerHandler.setGateConnCtx(ctx);
            logger.info("[GateServer to LogicServer] connection is established.");

        } else if(from == Internal.Greet.From.Auth) {
            LogicServerHandler.setAuthConnCtx(ctx);
            logger.info("[AuthServer to LogicServer] connection is established.");
        }

    }
}







