package com.tao.protobuf.constant;

public class PtoNum {

	private PtoNum() {

	}

	// 首先, 定义各种协议类对应的ptoNum
	// internal
	public static final int GTRANSFER = 901;		//服务器间传输协议的ptoNum
	public static final int GREET = 902;            //服务器连接问候协议的ptoNum

	// auth
	public static final int CREGISTER = 1001;       //用户注册协议的ptoNum
	public static final int CLOGIN = 1002;          //用户登录协议的ptoNum
	public static final int SRESPONSE = 1003;       //服务器对于认证服务的应答协议的ptoNum

	// chat
	public static final int CCHATMSG = 2001;        //客户端聊天协议的ptoNum
	public static final int CHATSRESPONSE = 2002;   //服务器收到聊天消息的应答协议的ptoNum
}
