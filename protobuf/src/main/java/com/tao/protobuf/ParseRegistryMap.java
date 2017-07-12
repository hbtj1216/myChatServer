package com.tao.protobuf;


import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.constant.PtoNum;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.message.internal.Internal;

/**
 * 注册解析函数和被解析的类。
 * @author Tao
 *
 */
public class ParseRegistryMap {
	
	
	/**
	 * 向ParseMap中注册ptoNum和对应的解析函数和类的Class对象。
	 */
	public static void initRegistry() {
		
		//internal(内部)
		ParseMap.register(PtoNum.GTRANSFER, Internal.GTransfer::parseFrom, Internal.GTransfer.class);
		ParseMap.register(PtoNum.GREET, Internal.Greet::parseFrom, Internal.Greet.class);
		
		//auth
		ParseMap.register(PtoNum.CREGISTER, Auth.CRegister::parseFrom, Auth.CRegister.class);
		ParseMap.register(PtoNum.CLOGIN, Auth.CLogin::parseFrom, Auth.CLogin.class);
		ParseMap.register(PtoNum.SRESPONSE, Auth.SResponse::parseFrom, Auth.SResponse.class);
		
		//chat
		ParseMap.register(PtoNum.CCHATMSG, Chat.CChatMsg::parseFrom, Chat.CChatMsg.class);
		ParseMap.register(PtoNum.SCHATMSG, Chat.SChatMsg::parseFrom, Chat.SChatMsg.class);
	}
	
}















