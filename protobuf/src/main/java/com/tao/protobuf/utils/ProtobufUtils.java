package com.tao.protobuf.utils;

import com.google.protobuf.Message;

import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.internal.Internal;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


/**
 * protobuf的工具类。
 * @author Tao
 *
 */
public class ProtobufUtils {
	
	private ProtobufUtils() {
		
	}
	
	
	/**
	 * 将消息体包装成服务器间GTransfer消息体, 并转换成ByteBuf对象。
	 * @param dest
	 * @param netId
	 * @param userId
	 * @param ptoNum
	 * @param msg
	 * @return
	 */
	public static ByteBuf pack2Server(Internal.DestType dest, long netId, String userId, int ptoNum, Message msg) {
		
		//通过Builder构建gTransfer对象。
		Internal.GTransfer.Builder gtb = Internal.GTransfer.newBuilder();
		gtb.setDest(dest)
			.setNetId(netId)
			.setUserId(userId)
			.setPtoNum(ptoNum)
			.setMsg(msg.toByteString());
		
		byte[] bytes = gtb.build().toByteArray();
		int length = bytes.length;
		int protoNum = PtoNum.GTRANSFER;
		
		ByteBuf buf = Unpooled.buffer(8 + length);
		buf.writeInt(length);
		buf.writeInt(protoNum);
		buf.writeBytes(bytes);
		
		return buf;
	}
	
	
	/**
	 * 将消息体包装成服务器间GTransfer消息体, 并转换成ByteBuf对象。
	 * netId为空。
	 * @param dest
	 * @param userId
	 * @param ptoNum
	 * @param msg
	 * @return
	 */
	public static ByteBuf pack2Server(Internal.DestType dest, String userId, int ptoNum, Message msg) {
		
		Internal.GTransfer.Builder gtb = Internal.GTransfer.newBuilder();
		gtb.setDest(dest)
			.setUserId(userId)
			.setPtoNum(ptoNum)
			.setMsg(msg.toByteString());
		
		byte[] bytes = gtb.build().toByteArray();
		int length = bytes.length;
		int protoNum = PtoNum.GTRANSFER;
		
		ByteBuf buf = Unpooled.buffer(8 + length);
		buf.writeInt(length);
		buf.writeInt(protoNum);
		buf.writeBytes(bytes);
		
		return buf;
	}
	
	
	/**
	 * 将消息封装成发往客户端的消息, 返回ByteBuf对象。
	 * @param msg
	 * @return
	 */
	public static ByteBuf pack2Client(Message msg) {
		
		byte[] bytes = msg.toByteArray();
		int length = bytes.length;
		int ptoNum = ParseMap.getPtoNum(msg);
		
		ByteBuf buf = Unpooled.buffer(8 + length);
		buf.writeInt(length);
		buf.writeInt(ptoNum);
		buf.writeBytes(bytes);
		
		return buf;
	}


    /**
     * 将消息封装成发往服务器端的消息, 返回ByteBuf对象.
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











