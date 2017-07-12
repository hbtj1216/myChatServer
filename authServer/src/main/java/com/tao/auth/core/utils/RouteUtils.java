package com.tao.auth.core.utils;

/**
 * Created by michael on 17-7-12.
 */

import com.tao.auth.core.handler.AuthServerHandler;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ProtobufUtils;
import io.netty.buffer.ByteBuf;

/**
 * 路由工具类
 */
public final class RouteUtils {


    private RouteUtils() {}


    /**
     * 向客户端回复消息.
     * @param code
     * @param content
     * @param netId
     * @param userId
     */
    public static void sendResponse(int code, String content, long netId, String userId) {

        Auth.SResponse.Builder sb = Auth.SResponse.newBuilder();
        sb.setCode(code);
        sb.setContent(content);
        Auth.SResponse sResponse = sb.build();

        //sResponse消息
        ByteBuf sendBuf = ProtobufUtils.pack2Server(Internal.DestType.Client,
                            netId, userId, PtoNum.SRESPONSE, sResponse);

        //通过gateServer转发给客户端.
        AuthServerHandler.getGateConnCtx().writeAndFlush(sendBuf);
    }

}









