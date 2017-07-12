package com.tao.logic.core.handler.messageHandler;

/**
 * Created by tao on 2017/7/6.
 */

import com.google.protobuf.Message;
import com.tao.logic.core.component.Worker;
import com.tao.logic.core.handler.MsgHandler;
import com.tao.protobuf.message.client2server.chat.Chat;
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

        //获得CChatMsg对象
        Chat.CChatMsg cChatMsg = (Chat.CChatMsg) msg;

    }
}











