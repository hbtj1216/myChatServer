package com.tao.protobuf.constant;

/**
 * Created by michael on 17-7-14.
 */

/**
 * 通用的常量.
 */
public class Common {

    private Common() {}

    //注册相关
    /**
     * 账号已经被注册
     */
    public static final int ACCOUNT_ALREADY_EXIST = 1000;

    /**
     * 注册成功
     */
    public static final int REGISTER_OK = 1001;


    //登录相关
    /**
     * 账号不存在, 未注册
     */
    public static final int ACCOUNT_INEXIST = 2000;

    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 2001;

    /**
     * 登录失败
     */
    public static final int LOGIN_ERROR = 2002;

    /**
     * 重复登录
     */
    public static final int REPEATED_LOGIN = 2003;


    //聊天相关
    /**
     * 消息发送者离线
     */
    public static final int CHAT_SENDER_OFFLINE = 3000;

    /**
     * 消息接收者离线
     */
    public static final int CHAT_RECEIVER_OFFLINE = 3001;

    /**
     * 服务器接收客户端消息成功
     */
    public static final int CHAT_MESSAGE_RECEIVE_SUCCESS = 3002;

    /**
     * 服务器接收客户端消息失败
     */
    public static final int CHAT_MESSAGE_RECEIVE_FAILED = 3003;
}
