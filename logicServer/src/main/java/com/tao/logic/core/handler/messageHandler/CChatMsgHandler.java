package com.tao.logic.core.handler.messageHandler;

/**
 * Created by tao on 2017/7/6.
 */

import com.google.protobuf.Message;
import com.tao.logic.core.component.Worker;
import com.tao.logic.core.handler.LogicServerHandler;
import com.tao.logic.core.handler.MsgHandler;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ServerProtoUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端发来的CChatMsg消息的handler.
 */
public final class CChatMsgHandler extends MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(CChatMsgHandler.class);


    public CChatMsgHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
    }


    @Override
    public void excute(Worker worker) throws Exception {

        //处理聊天消息

        //获得CChatMsg对象
        Chat.CChatMsg cChatMsg = (Chat.CChatMsg) msg;
        logger.info("LogicServer收到了聊天消息: {}", cChatMsg);

        //TODO
        //在这里服务器可以对用户的聊天消息做一些收集或者处理。
        //如果不想处理, 那么直接发回GateServer.

        //包装成GTransfer转发给GateServer
        ByteBuf sendBuf = ServerProtoUtils.pack2Server(Internal.DestType.Client,
                netId, userId, ParseMap.getPtoNum(cChatMsg), cChatMsg);
        //发送给GateServer
        LogicServerHandler.getGateConnCtx().writeAndFlush(sendBuf);
        logger.info("[LogicServer] send [{}] message to [GateServer]", cChatMsg.getClass().getSimpleName());
    }
}











