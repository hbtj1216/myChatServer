package com.tao.client;

/**
 * Created by michael on 17-7-12.
 */
public final class ClientConstant {

    private ClientConstant() {}

    //注册相关
    public static final int ACCOUNT_ALREADY_EXIST = 100;
    public static final int REGISTER_OK = 101;

    //登录相关
    public static final int ACCOUNT_INEXIST = 200;
    public static final int LOGIN_SUCCESS = 201;
    public static final int LOGIN_ERROR = 202;

    //聊天相关
    public static final int Msg_SendSuccess= 300;
}
