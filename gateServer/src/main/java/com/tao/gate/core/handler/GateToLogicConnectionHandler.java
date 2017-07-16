package com.tao.gate.core.handler;

import com.sun.security.ntlm.Server;
import com.tao.gate.core.domain.ClientConnection;
import com.tao.gate.core.global.maps.ClientConnectionMap;
import com.tao.gate.core.utils.RouteUtils;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ServerProtoUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;


/**
 * GateServer到LogicServer连接的消息处理类。
 * @author Tao
 *
 */
public class GateToLogicConnectionHandler extends SimpleChannelInboundHandler<Message> {
	
	private static final Logger logger = LoggerFactory.getLogger(GateToLogicConnectionHandler.class);


	//静态域
	/**
	 * GateServer 到 LogicServer 的连接的ctx
	 */
	private static ChannelHandlerContext gateToLogicConnectionContext;


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		gateToLogicConnectionContext = ctx;
		logger.info("[GateServer to LogicServer] 连接已经建立成功.");

		//向LogicServer发送Greet协议消息
		sendGreetToLogicServer();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		gateToLogicConnectionContext = null;
		logger.info("[GateServer to LogicServer] 连接已经断开.");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

		//收到LogicServer处理之后的消息
		logger.info("[GateServer] 收到经过[LogicServer]处理之后的消息[{}].", msg.getClass().getSimpleName());

		//解析消息
        Internal.GTransfer gTransfer = (Internal.GTransfer) msg;

        Internal.DestType dest = gTransfer.getDest();

        if(dest == Internal.DestType.Client) {
            //如果目的地: 客户端
            //获得message
            Message message = ParseMap.getMessage(gTransfer.getPtoNum(), gTransfer.getMsg().toByteArray());

            if(message instanceof Chat.CChatMsg) {

                handleChatMessage(message);
            }

        }


		
	}


	public static ChannelHandlerContext getGateToLogicConnectionContext() {
		return gateToLogicConnectionContext;
	}



	/**
	 * 向AuthServer发送Greet协议消息
	 */
	private void sendGreetToLogicServer() {

		Internal.Greet.Builder greetB = Internal.Greet.newBuilder();
		greetB.setFrom(Internal.Greet.From.Gate);

		//包装成GTranser
		ByteBuf buf = ServerProtoUtils.pack2Server(Internal.DestType.Logic,
				-1, "admin", PtoNum.GREET, greetB.build());

		//发送给LogicServer
		ChannelFuture future = gateToLogicConnectionContext.writeAndFlush(buf);
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				logger.info("GateServer 向 LogicServer 发送了 Greet 消息.");
			}
		});

	}


    /**
     * 处理聊天消息的发送.
     * @param message
     */
	private void handleChatMessage(Message message) {

        Chat.CChatMsg cChatMsg = (Chat.CChatMsg) message;

        String senderId = cChatMsg.getSenderId();
        String receiverId = cChatMsg.getReceiverId();

        //根据netId判断是发送给个人还是发送给所有人
        if("all".equalsIgnoreCase(receiverId)) {
            //发送给所有人
            ByteBuf sendBuf = ServerProtoUtils.pack2Client(cChatMsg);
            //得到所有在线用户的netId的集合
            List<Long> netIdList = ClientConnectionMap.getAllNetId();
            logger.info("[GateServer] 向所有的在线用户发送聊天消息!");
            for(Long netId : netIdList) {
                ClientConnection clientConnection = ClientConnectionMap.getClientConnection(netId);
                //发送消息
                clientConnection.getCtx().writeAndFlush(sendBuf);
            }

        } else {
            //发送给个人
            //得到两个netId
            Long senderNetId = ClientConnectionMap.getNetIdByUserId(senderId);
            Long receiverNetId = ClientConnectionMap.getNetIdByUserId(receiverId);
            //得到两个ClientConnection
            ClientConnection senderConn = ClientConnectionMap.getClientConnection(senderNetId);
            ClientConnection receiverConn = ClientConnectionMap.getClientConnection(receiverNetId);

            //发送给接收者
            logger.info("[GateServer] 向 {} 发送聊天消息!", receiverId);
            ByteBuf sendBuf = ServerProtoUtils.pack2Client(cChatMsg);
            receiverConn.getCtx().writeAndFlush(sendBuf);
        }



    }
}
