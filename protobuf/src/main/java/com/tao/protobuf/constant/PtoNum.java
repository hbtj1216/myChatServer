package com.tao.protobuf.constant;

public class PtoNum {

	private PtoNum() {

	}

	// 首先, 定义各种协议类对应的ptoNum
	// internal
	public static final int GTRANSFER = 901;
	public static final int GREET = 902;

	// auth
	public static final int CREGISTER = 1001;
	public static final int CLOGIN = 1002;
	public static final int SRESPONSE = 1003;

	// chat
	public static final int CCHATMSG = 2001;
	public static final int SCHATMSG = 2002;
}
