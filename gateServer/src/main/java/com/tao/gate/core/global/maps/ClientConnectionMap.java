package com.tao.gate.core.global.maps;

import java.util.concurrent.ConcurrentHashMap;

import com.tao.gate.core.domain.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.channel.ChannelHandlerContext;


/**
 * 缓存一个gateserver上的所有客户端连接。
 * @author Tao
 *
 */
public class ClientConnectionMap {

	private static final Logger logger = LoggerFactory.getLogger(ClientConnectionMap.class);
	
	//保存一个gateServer上的所有客户端的连接,键为netId, 值为clientConnection
	private static ConcurrentHashMap<Long, ClientConnection> allClientMap = new ConcurrentHashMap<>();
	//维护userId和其对应的netId的map
	private static ConcurrentHashMap<String, Long> userId2netIdMap = new ConcurrentHashMap<>();
	
	
	private ClientConnectionMap() {

	}
	
	
	/**
	 * 通过netId获取对应的clientConnection
	 * @param netId
	 * @return
	 */
	public static ClientConnection getClientConnection(long netId) {
		
		ClientConnection connection = allClientMap.get(netId);
		if(connection != null) {
			return connection;
		}
		else {
			logger.error("ClientConnectionMap中没有找到netId 为 {} 对应的clientConnection对象。", netId);
			return null;
		}
	}
	
	
	/**
	 * 通过channelHandlerContext获取对应的clientConnection
	 * @param ctx
	 * @return
	 */
	public static ClientConnection getClientConnection(ChannelHandlerContext ctx) {
		
		//获取netId
		Long netId = ctx.channel().attr(ClientConnection.NETID).get();
		ClientConnection connection = allClientMap.get(netId);
		if(connection != null) {
			return connection;
		}
		else {
			logger.error("ClientConnectionMap中没有找到对应的clientConnection对象。");
			return null;
		}
	}
	
	
	/**
	 * 向map中添加clientConnection
	 * @param clientConnection
	 */
	public static void addClientConnection(ClientConnection clientConnection) {
		
		long netId = clientConnection.getNetId();
		String userId = clientConnection.getUserId();
		//将clientConnection添加到map中
		//putIfAbsent会保证如果value != null, 不会覆盖原来的值
		if(allClientMap.putIfAbsent(netId, clientConnection) != null) {
			logger.info("clientConnection添加成功。");
			//注册userId
			registerUserId(userId, netId);
		}
		else {
			logger.error("NetId: {} 不存在与map中。", netId);
			return;
		}
		
	}
	
	
	/**
	 * 从map中删除对应的clientConnection
	 * @param ctx
	 */
	public static void removeClientConnection(ChannelHandlerContext ctx) {
		
		//获得clientConnection
		ClientConnection conn = getClientConnection(ctx);
		if(conn != null) {
			String userId = conn.getUserId();
			Long netId = conn.getNetId();
			if(allClientMap.remove(netId) != null) {
				logger.info("clientConnection移除成功。");
				//注销userId
				unRegisterUserId(userId);
			}
			else {
				logger.error("NetId: {} 不存在与map中。", netId);
				return;
			}
		}
		
	}
	
	
	/**
	 * 从map中移除clientConnection
	 * @param clientConnection
	 */
	public static void removeClientConnection(ClientConnection clientConnection) {
		
		long netId = clientConnection.getNetId();
		String userId = clientConnection.getUserId();
		if(allClientMap.remove(netId) != null) {
			logger.info("clientConnection移除成功。");
			//注销userId
			unRegisterUserId(userId);
		}
		else {
			logger.error("NetId: {} 不存在与map中。", netId);
			return;
		}
	}
	
	
	/**
	 * 注册userId
	 * @param userId
	 * @param netId
	 */
	public static void registerUserId(String userId, long netId) {
		
		if(userId2netIdMap.putIfAbsent(userId, netId) != null) {
			logger.info("userId={} 注册成功.", userId);
		}
		else {
			logger.error("userId={} 注册失败.", userId);
			return;
		}
	}
	
	
	/**
	 * 注销userId
	 * @param userId
	 */
	public static void unRegisterUserId(String userId) {
		
		if(userId == null) {
			return;
		}
		if(userId2netIdMap.remove(userId) != null) {
			logger.info("userId={} 注销成功.", userId);
		}
		else {
			logger.error("map中不存在userId={}的键.", userId);
		}
	}
	
	
	/**
	 * 获得userId对应的netId
	 * @return
	 */
	public static Long getNetIdByUserId(String userId) {
		
		Long netId = userId2netIdMap.get(userId);
		if(netId != null) {
			return netId;
		}
		else {
			logger.error("用户未登陆, userid: {}", userId);
			return null;
		}
	}
	
}















