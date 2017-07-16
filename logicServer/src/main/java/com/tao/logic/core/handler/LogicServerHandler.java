package com.tao.logic.core.handler;

import com.google.protobuf.Message;
import com.tao.logic.core.component.LogicCenter;
import com.tao.logic.core.global.HandlerManager;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.message.internal.Internal;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tao on 2017/7/6.
 */

/**
 * LogicServer的handler.
 * 负责处理来自LogicServer的消息.
 */
public final class LogicServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(LogicServerHandler.class);

    private LogicCenter logicCenter;    //逻辑消息处理中心

    private static ChannelHandlerContext gateConnCtx;   //gate连接的ctx


    public LogicServerHandler(LogicCenter logicCenter) {
        this.logicCenter = logicCenter;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        /**
         * LogicServer收到了GateServer发来的消息.
         */

        Internal.GTransfer gt = (Internal.GTransfer) msg;
        //获得ptoNum
        int ptoNum = gt.getPtoNum();
        //获得消息对象
        Message message = ParseMap.getMessage(ptoNum, gt.getMsg().toByteArray());

        //判断消息类型
        MsgHandler msgHandler = null;
        if(message instanceof Internal.Greet) {
            //如果是Greet消息
            logger.info("[LogicServer] 收到来自 [GateServer] 的 Greet 消息.");
            msgHandler = HandlerManager.getMsgHandler(ptoNum, gt.getUserId(), gt.getNetId(),
                    message, ctx);
        } else if(message instanceof Chat.CChatMsg) {
            //如果是聊天消息
            logger.info("[LogicServer] 收到来自 [GateServer] 的 {} 消息.", message.getClass().getSimpleName());
            msgHandler = HandlerManager.getMsgHandler(ptoNum, gt.getUserId(), gt.getNetId(),
                    message, getGateConnCtx());
        }

        //将消息分发给LogicCenter中的Worker线程进行处理
        logger.debug("userId: {}, msgHandler: {}", gt.getUserId(), msgHandler);
        logicCenter.distributeWork(gt.getUserId(), msgHandler);



    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
    }



    public static ChannelHandlerContext getGateConnCtx() {
        return gateConnCtx;
    }

    public static void setGateConnCtx(ChannelHandlerContext gateConnCtx) {
        LogicServerHandler.gateConnCtx = gateConnCtx;
    }


}









