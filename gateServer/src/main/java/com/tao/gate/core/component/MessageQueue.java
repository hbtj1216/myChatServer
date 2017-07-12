package com.tao.gate.core.component;


import com.tao.gate.core.domain.RequestMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息队列。
 * 每个用户的连接都有一个消息队列。
 * @author tao
 *
 */
public class MessageQueue {
	
	//客户端请求的消息队列
	private BlockingQueue<RequestMessage> requestMessageQueue;
	private int capacity;	//容量
	
	
	/**
	 * 构建一个使用默认容量的消息队列
	 */
	public MessageQueue() {
		this.requestMessageQueue = new LinkedBlockingQueue<>();
		this.capacity = this.requestMessageQueue.remainingCapacity();
	}
	
	
	/**
	 * 构建一个带指定容量的消息队列
	 * @param capacity
	 */
	public MessageQueue(int capacity) {
		if(capacity <= 0) {
			return;
		}
		this.capacity = capacity;
		this.requestMessageQueue = new LinkedBlockingQueue<>(this.capacity);
	}
	
	
	/**
	 * 向消息队列中添加元素, 如果队列已经满了, 当前线程会被阻塞。
	 * @param requestMessage
	 */
	public void add(RequestMessage requestMessage) {
		
		try {
			this.requestMessageQueue.put(requestMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 从消息队列中取出元素, 如果消息队列已经空了, 当前线程会被阻塞直到取到元素为止。
	 * @return
	 */
	public RequestMessage take() {
		
		try {
			return this.requestMessageQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 清空当前消息队列中的所有内容。
	 */
	public void clear() {
		
		this.requestMessageQueue.clear();
		this.requestMessageQueue = null;
	}
	
	
	/**
	 * 返回当前消息队列中的元素的个数。
	 * @return
	 */
	public int size() {
		
		if(this.requestMessageQueue != null) {
			return this.requestMessageQueue.size();
		}
		else {
			return 0;
		}
	}
	
	
}















