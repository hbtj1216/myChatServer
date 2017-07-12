package com.tao.gate.core.domain;

import com.google.protobuf.Message;

/**
 * 客户端请求的消息。
 * 封装了ClientConnection和对应的protobuf Message.
 * @author tao
 *
 */
public final class RequestMessage {
	
	private ClientConnection clientConnection;	//客户端的连接
	private Message message;					//客户端发送过来的消息
	
	
	public RequestMessage(ClientConnection clientConnection, Message message) {

		this.clientConnection = clientConnection;
		this.message = message;
	}


	public ClientConnection getClientConnection() {
		return clientConnection;
	}


	public Message getMessage() {
		return message;
	}


	public void setClientConnection(ClientConnection clientConnection) {
		this.clientConnection = clientConnection;
	}


	public void setMessage(Message message) {
		this.message = message;
	}


	@Override
	public String toString() {
		return "RequestMessage [clientConnection=" + clientConnection + ", message=" + message + "]";
	}
		
}
