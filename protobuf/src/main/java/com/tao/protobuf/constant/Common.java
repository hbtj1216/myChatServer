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
    public static final int ACCOUNT_ALREADY_EXIST = 100;

    /**
     * 注册成功
     */
    public static final int REGISTER_OK = 101;

    
    //登录相关
    /**
     * 账号不存在, 未注册
     */
    public static final int ACCOUNT_INEXIST = 200;

    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 201;

    /**
     * 登录失败
     */
    public static final int LOGIN_ERROR = 202;

    /**
     * 重复登录
     */
    public static final int REPEATED_LOGIN = 203;


    //聊天相关
    /**
     * 消息发送者离线
     */
    public static final int CHAT_SENDER_OFFLINE = 300;

    /**
     * 消息接收者离线
     */
    public static final int CHAT_RECEIVER_OFFLINE = 301;
}
