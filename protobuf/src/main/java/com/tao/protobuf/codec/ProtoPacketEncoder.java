package com.tao.protobuf.codec;

import com.tao.protobuf.analysis.ParseMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 对protobuf消息包进行编码的 编码器。
 * @author Tao
 *
 */
public class ProtoPacketEncoder extends MessageToByteEncoder<Message> {
	
	private static final Logger logger = LoggerFactory.getLogger(ProtoPacketEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {

	    logger.info("编码器被调用. message : {}", msg.getClass().getSimpleName());
		//这里的Message类是protobuf协议中的message
		//将msg转换成bytes数组
		byte[] bytes = msg.toByteArray();
		//获得msg对应的ptoNum的值
		int ptoNum = ParseMap.msg2ptoNum.get(msg);
		//计算消息内容的长度
		int length = bytes.length;
		
		//加密TODO
		
		//组装成ByteBuf对象，发送出去
		ByteBuf buf = Unpooled.buffer(8+length);	//单位:字节
		//length(4字节) + ptoNum(4字节) + bytes(length字节)
		buf.writeInt(length);
		buf.writeInt(ptoNum);
		buf.writeBytes(bytes);

		logger.info("发送了一条消息。remoteAddress: {}, "
				+ "content length {}, ptoNum: {}", ctx.channel().remoteAddress(), length, ptoNum);

		//发送
		out.writeBytes(buf);

	}
	
}
