package com.tao.protobuf.codec;

import java.util.List;

import com.tao.protobuf.analysis.ParseMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 对protobuf消息包进行解码的 解码器。
 * @author Tao
 *
 */
public class ProtoPacketDecoder extends ByteToMessageDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(ProtoPacketDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		logger.info("解码器被调用.");

		in.markReaderIndex();
		
		//如果可读的字节数 < 4字节, 直接忽略
		if(in.readableBytes() < 4) {
			logger.info("可读字节数小于4字节, 忽略消息.");
			//重置readerIndex
			in.resetReaderIndex();
			return;
		}
		
		/**
		 * 开始读取消息。
		 * 消息的结构为：
		 * 消息长度length(4字节) + ptoNum(4字节) + 消息内容(length字节)
		 */
		
		//获取消息的长度
		int length = in.readInt();
		if(length < 0) {
			ctx.close();
			logger.error("消息长度小于0字节, 关闭当前channel.");
            return;
		}
		if(length > in.readableBytes() - 4) {
			in.resetReaderIndex();
			return;
		}
		
		//读取ptoNum
		int ptoNum = in.readInt();
		
		//读取消息内容到content
		ByteBuf content = Unpooled.buffer(length);
		in.readBytes(content);
		
		try {
			
			//消息内容的字节数组
			byte[] body = content.array();
			
			//根据ptoNum和消息内容的字节数组, 获得对应的protobuf Message对象
			Message msg = ParseMap.getMessage(ptoNum, body);
			out.add(msg);
			logger.info("收到一条消息: content length {}, ptoNum: {}, msg: {}", length, ptoNum,
					msg.getClass().getSimpleName());
			
		} catch (Exception e) {
			logger.error(ctx.channel().remoteAddress() + ",解码失败.", e);
		}
		
	}

}











