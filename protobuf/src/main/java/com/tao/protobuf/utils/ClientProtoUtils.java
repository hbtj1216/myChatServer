package com.tao.protobuf.utils;

/**
 * Created by michael on 17-7-16.
 */

import com.google.protobuf.Message;
import com.tao.protobuf.analysis.ParseMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 客户端使用的Protobuf消息封装工具.
 */
public class ClientProtoUtils {


    private ClientProtoUtils() {}


    /**
     * 将客户端将要发送的消息封装成发往服务器端的消息, 返回ByteBuf对象.
     * @param msg
     * @return
     */
    public static ByteBuf pack2Server(Message msg) {

        byte[] bytes = msg.toByteArray();
        int length = bytes.length;
        int ptoNum = ParseMap.getPtoNum(msg);

        ByteBuf sendBuf = Unpooled.buffer(8 + length);
        sendBuf.writeInt(length);
        sendBuf.writeInt(ptoNum);
        sendBuf.writeBytes(bytes);

        return sendBuf;
    }
}
