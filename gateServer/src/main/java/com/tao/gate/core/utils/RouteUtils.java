package com.tao.gate.core.utils;

import com.tao.gate.core.domain.ClientConnection;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.utils.ServerProtoUtils;
import io.netty.buffer.ByteBuf;

/**
 * Created by michael on 17-7-14.
 */
public class RouteUtils {

    /**
     * 向客户端发送返回的消息.
     * @param code
     * @param content
     * @param clientConnection
     */
    public static void sendResponse(int code, String content, ClientConnection clientConnection) {

        Auth.SResponse.Builder sb = Auth.SResponse.newBuilder();
        sb.setCode(code);
        sb.setContent(content);
        Auth.SResponse sResponse = sb.build();

        //sResponse消息
        ByteBuf sendBuf = ServerProtoUtils.pack2Client(sResponse);

        //发给客户端.
        clientConnection.getCtx().writeAndFlush(sendBuf);
    }


    /**
     * 给消息发送者回话
     */
    public static void sendResponse2Sender(int code, String content, ClientConnection clientConnection) {

        Chat.SResponse.Builder sb = Chat.SResponse.newBuilder();
        sb.setCode(code);
        sb.setContent(content);
        Chat.SResponse sResponse = sb.build();

        ByteBuf sendBuf = ServerProtoUtils.pack2Client(sResponse);

        //发给客户端
        clientConnection.getCtx().writeAndFlush(sendBuf);
    }

}





