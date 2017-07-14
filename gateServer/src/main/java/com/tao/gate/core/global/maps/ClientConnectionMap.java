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
	
	//保存一个gateServer上的所有客户端的连接(连接不代表登录),键为netId, 值为clientConnection
	private static ConcurrentHashMap<Long, ClientConnection> allClientConnectionMap = new ConcurrentHashMap<>();
	//维护userId和其对应的netId的map(登录用户的缓存)
	private static ConcurrentHashMap<String, Long> userId2netIdMap = new ConcurrentHashMap<>();
	
	
	private ClientConnectionMap() {

	}
	
	
	/**
	 * 通过netId获取对应的clientConnection
	 * @param netId
	 * @return
	 */
	public static ClientConnection getClientConnection(long netId) {
		
		ClientConnection connection = allClientConnectionMap.get(netId);
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
		ClientConnection connection = allClientConnectionMap.get(netId);
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
		//logger.info("将要被添加到缓存中的ClientConnection. netId: {}, userId: {}", netId, userId);
		//将clientConnection添加到map中
		//putIfAbsent会保证如果value != null, 不会覆盖原来的值
		//logger.info("添加之前allClientMap: {}", allClientMap);
		if(allClientConnectionMap.putIfAbsent(netId, clientConnection) != null) {
		    //返回值 ！= null, 说明已经存在
			logger.info("clientConnection已经存在, netId: {}", netId);
		} else {
		    //返回值 == null, 说明是新的值, 并已经添加到了map中
			logger.error("新用户的clientConnection已经添加到map中, netId: {}", netId);
		}
		//logger.info("添加之后allClientMap: {}", allClientMap);
		
	}

	
	/**
	 * 从map中移除clientConnection
	 * @param clientConnection
	 */
	public static void removeClientConnection(ClientConnection clientConnection) {
		
		long netId = clientConnection.getNetId();
		String userId = clientConnection.getUserId();
		if(allClientConnectionMap.remove(netId) != null) {
			logger.info("从缓存中注销clientConnection.");
			//注销userId
			unRegisterLoginUser(userId);
		}
		else {
			logger.error("netId: {} 不存在与map中。", netId);
			return;
		}
	}
	
	
	/**
	 * 缓存已经登陆的用户.
	 * @param userId
	 * @param netId
	 */
	public static void registerLoginUser(String userId, long netId) {

	    if(userId2netIdMap.putIfAbsent(userId, netId) == null) {
	        //返回值 == null, 说明是新的登陆的用户
            logger.info("新用户登录! userId: {}, netId: {}", userId, netId);

            ClientConnection conn = ClientConnectionMap.getClientConnection(netId);
            logger.info("userId: {} 对应的的clientConnection: {}", userId, conn);
            if(conn != null) {
                conn.setUserId(userId);
            } else {
                logger.error("userId: {} 对应的clientConnection为null.", userId);
            }
            ClientConnection coon2 = ClientConnectionMap.getClientConnection(netId);
            logger.info("register之后的conn: {}", coon2);
        } else {
	        //返回值 ！= null, 说明该用户已经登录
            logger.error("userId: {} 用户已经登录, 并在登录缓存中注册过!", userId);
        }

	}
	
	/**
	 * 注销登陆的用户.
	 * @param userId
	 */
	public static void unRegisterLoginUser(String userId) {
		
		if(userId == null) {
			return;
		}
		if(userId2netIdMap.remove(userId) != null) {
			logger.info("userId={} 用户注销成功.", userId);
		}
		else {
			logger.error("map中不存在userId={}的键.", userId);
		}
	}


    /**
     * 判断用户是否已经登录.
     * @param userId
     * @return
     */
	public static boolean isUserLogin(String userId) {

		Long netId = userId2netIdMap.get(userId);
		if(netId != null) {
			//已经登录并缓存
			return true;
		} else {
			logger.error("用户未登陆, userid: {}", userId);
			return false;
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















