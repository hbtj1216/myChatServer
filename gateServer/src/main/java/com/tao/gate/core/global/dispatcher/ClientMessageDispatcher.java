package com.tao.gate.core.global.dispatcher;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import com.tao.gate.core.component.MessageQueue;
import com.tao.gate.core.domain.ClientConnection;
import com.tao.gate.core.domain.RequestMessage;
import com.tao.gate.core.global.maps.ClientConnectionMap;
import com.tao.gate.core.handler.GateToAuthConnectionHandler;
import com.tao.gate.core.handler.GateToLogicConnectionHandler;
import com.tao.gate.core.utils.RouteUtils;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.message.internal.Internal;
import com.tao.protobuf.utils.ServerProtoUtils;
import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;



/**
 * GateServer中对客户端消息进行分发的 分发器。
 * clientMessageDispatcher工作在单独的线程中, 不断地从消息队列中取得ClientMessage, 然后分发。
 * @author Tao
 *
 */
public final class ClientMessageDispatcher implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientMessageDispatcher.class);

	/**
	 * 分发器中的session缓存区, 用于缓存客户端连接及其对应的消息队列.
	 * 每一个netId都对应一个MessageQueue.
	 * key = netId, value = 该客户端连接的消息队列
	 */
	private final Map<Long, MessageQueue> clientSessionMap;
	
	//线程池(处理每个messageQueue)
	private Executor handleMessageQueueExecutor;
	
	//轮询周期
	private long sleepTime;
	
	
	//构造函数
	public ClientMessageDispatcher() {
		
		this.clientSessionMap = new ConcurrentHashMap<>();
		this.handleMessageQueueExecutor = Executors.newFixedThreadPool(10);
		this.sleepTime = 200L;	//200毫秒
	}
		
	
	/**
	 * 向分发器的缓存中添加客户端channel对应的netId 和 该客户端的messageQueue。
	 * @param netId
	 * @param messageQueue
	 */
	public void addMessageQueue(Long netId, MessageQueue messageQueue) {
		
		this.clientSessionMap.put(netId, messageQueue);
	}
	
	public void addMessageQueue(ClientConnection clientConnection, MessageQueue messageQueue) {
		
		this.clientSessionMap.put(clientConnection.getNetId(), messageQueue);
	}
	
	
	/**
	 * 返回netId对应的客户端的messageQueue。
	 * @param netId
	 * @return
	 */
	public MessageQueue getMessageQueue(Long netId) {
		
		return this.clientSessionMap.get(netId);
	}
	
	public MessageQueue getMessageQueue(ClientConnection clientConnection) {
		
		return this.clientSessionMap.get(clientConnection.getNetId());
	}
	
	
	
	/**
	 * 从分发器中的缓存中删除netId对应的messageQueue
	 * @param netId
	 */
	public void removeMessageQueue(Long netId) {
		
		MessageQueue messageQueue = this.clientSessionMap.remove(netId);
		if(messageQueue != null) {
			messageQueue.clear();
		}
	}
	
	public void removeMessageQueue(ClientConnection clientConnection) {
		
		MessageQueue messageQueue = this.clientSessionMap.remove(clientConnection.getNetId());
		if(messageQueue != null) {
			messageQueue.clear();
		}
	}
	

	
	/**
	 * 将消息添加到对应的messageQueue中。
	 * @param clientConnection
	 * @param message
	 */
	public void addMessage(ClientConnection clientConnection, Message message) {
		
		//创建requestMessage对象
		RequestMessage requestMessage = new RequestMessage(clientConnection, message);
		Long netId = clientConnection.getNetId();
		//从map中查找netId对应的messageQueue
		MessageQueue messageQueue = this.clientSessionMap.get(netId);
		if(messageQueue == null) {
			//不存在, 创建一个新的messageQueue
			messageQueue = new MessageQueue();
			
			messageQueue.add(requestMessage);
			this.clientSessionMap.put(netId, messageQueue);
		}
		else {
			messageQueue.add(requestMessage);
		}
		logger.info("向 netId: {} 对应的MessageQueue中添加一个消息 message: {}.", netId,
                message.getClass().getSimpleName());
	}
	
	
	
	@Override
	public void run() {
		// clientMessageDispatcher分发器运行在单独的线程中。
		logger.info("clientMessageDispatcher开始运行...");
		logger.info("分发器正在运行中...");
		while(true) {
			
			//遍历所有的messageQueue
			for (MessageQueue messageQueue : this.clientSessionMap.values()) {
				//如果有元素
				if(messageQueue != null && messageQueue.size() > 0) {
					//获得connection
					//封装成一个Task, 送入线程池进行处理
					MessageQueueTask messageQueueTask = new MessageQueueTask(messageQueue);
					this.handleMessageQueueExecutor.execute(messageQueueTask);
				}
			}
			try {
				Thread.sleep(this.sleepTime);	//轮询的周期
				//logger.info("clientMessageDispatcher轮询一次, 周期为 {}", this.sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}





	/**
	 * 一个线程任务类。
	 * 用于封装一个messageQueue消息队列, 然后交由线程池进行任务的处理。
	 * @author tao
	 *
	 */
	private final class MessageQueueTask implements Runnable {


		private MessageQueue messageQueue;	            //消息队列的引用
		private RequestMessage requestMessage = null;	//requestMessage的引用



		//构造函数
		public MessageQueueTask(MessageQueue messageQueue) {

			this.messageQueue = messageQueue;
			/**
			 * 注意:
			 * 分发器主线程在一次轮询中会为每个非空的messageQueue创建一个对应的messageQueueTask对象。
			 * 这个essageQueueTask会从它对应的messageQueue中“只取”一个requestMessage进行处理。
			 * 如果messageQueue中有不止一个requestMessage, 会在下一次轮询中再取一个,
			 * 这样做的好处是, 对每个客户端都是公平的, 不会因为某个客户的messageQueue中的requestMessage太多,
			 * 而阻塞在这个客户端的requestMessage处理上。
			 */
			this.requestMessage = this.messageQueue.take();
		}


		/**
		 * 取得messageQueue中的一个message之后, 由线程池中的一个线程来处理它。
		 */
		@Override
		public void run() {

			try {

				//处理获得的requestMessage
				handleMessage(this.requestMessage);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		/**
		 * 处理message
		 * @param requestMessage
		 */
		private void handleMessage(RequestMessage requestMessage) {

			//获得内部的message对象
			Message message = requestMessage.getMessage();
			logger.info("dispatcher处理一个消息, messageName: [{}]", message.getClass().getSimpleName());

			//根据Message种类, 选择正确的处理方法
			/**
			 * 首先需要判断消息是要转发给AuthServer还是LogicServer。
			 * 通过ParseMap获取message对应的ptoNum, 然后转发。
			 * CRegister、CLogin转发给AuthServer处理;
			 * CChatMsg转发给LogicServer处理。
			 */
			int ptoNum = ParseMap.getPtoNum(message);

			switch (ptoNum) {

				/**
				 * 如果是客户端的注册、登录消息
				 */
				case PtoNum.CREGISTER:
				case PtoNum.CLOGIN:
					forwardToAuthServer(requestMessage);		//发送给AuthServer服务器处理
					break;

				/**
				 * 如果是客户端发送的的聊天消息
				 */
				case PtoNum.CCHATMSG:
                    forwardToLogicServer(requestMessage);		//发送给logicServer服务器处理
                    break;

				default:
					break;
			}

		}


		/**
		 * 转发给AuthServer处理.
		 * @param requestMessage
		 */
		private void forwardToAuthServer(RequestMessage requestMessage) {

			ClientConnection clientConnection = requestMessage.getClientConnection();
			Message message = requestMessage.getMessage();

			ByteBuf sendBuf = null;
			if(message instanceof Auth.CRegister) {
				//如果是注册消息, 先包装成服务器之间的协议体GTransfer
				sendBuf = ServerProtoUtils.pack2Server(Internal.DestType.Auth,
						clientConnection.getNetId(),
						((Auth.CRegister)message).getUserId(),
						ParseMap.getPtoNum(message), message);
			}
			else if(message instanceof Auth.CLogin) {

				//在发送CLogin消息之前, 需要先判断是否已经登录
                String userId = ((Auth.CLogin)message).getUserId();
				boolean isLogin = ClientConnectionMap.isUserLogin(userId);

                if(isLogin) {
                    //如果已经登录, 不允许重复登录
                    //向客户端发送重复登录的警告!
                    logger.info("账户已经登录, 不允许重复登录, userId: {}", userId);
                    RouteUtils.sendResponse(Common.REPEATED_LOGIN, "Repeated Login.", clientConnection);
                } else {
                    //如果未登录
                    //登录消息, 先包装成服务器之间的协议体GTransfer
                    sendBuf = ServerProtoUtils.pack2Server(Internal.DestType.Auth,
                            clientConnection.getNetId(),
                            ((Auth.CLogin)message).getUserId(),
                            ParseMap.getPtoNum(message), message);
                }

			}

			if(sendBuf != null) {
                //获取Gate->Auth的连接, 并将消息发送给AuthServer
                logger.info("GateServer send [{}] message to AuthServer.", message.getClass().getSimpleName());
                ChannelHandlerContext gate2AuthConnCtx = GateToAuthConnectionHandler.getGateToAuthConnectionContext();
                gate2AuthConnCtx.channel().writeAndFlush(sendBuf);
            }

		}



		/**
		 * 聊天消息转发给LogicServer处理.
		 * @param requestMessage
		 */
		private void forwardToLogicServer(RequestMessage requestMessage) {

			ClientConnection clientConnection = requestMessage.getClientConnection();
			Message message = requestMessage.getMessage();

			//先判断用户有没有登录
			if(clientConnection.getUserId() == null) {
				//用户Id为空, 说明没有登录就直接发消息了
				logger.info("User not login!");
				RouteUtils.sendResponse(Common.CHAT_SENDER_OFFLINE,
                        "Please login first!", clientConnection);

			} else {    //userId不为null, 说明用户已经登录

                //判断消息类型
                if(message instanceof Chat.CChatMsg) {

                    //首先给发送者(客户端)回应, 收到消息
                    /*RouteUtils.sendResponse2Sender(Common.CHAT_MESSAGE_RECEIVE_SUCCESS,
                        "Server receive message success.", clientConnection);*/

                    //客户端发送的聊天消息,封装成GTransfer发送给logicServer
                    ByteBuf sendBuf = ServerProtoUtils.pack2Server(Internal.DestType.Logic,
                            clientConnection.getNetId(), clientConnection.getUserId(),
                            ParseMap.getPtoNum(message), message);

                    //获得Gate到Logic的连接的ctx, 并将消息发送给LogicServer
                    ChannelHandlerContext gate2LogicConnCtx = GateToLogicConnectionHandler.getGateToLogicConnectionContext();
                    gate2LogicConnCtx.channel().writeAndFlush(sendBuf);
                    logger.info("[GateServer] send [{}] message to [LogicServer]", message.getClass().getSimpleName());
                }
            }

		}


	}
	
}














