package com.tao.auth.core.handler.messageHandler;

import com.google.protobuf.Message;
import com.tao.auth.core.component.Worker;
import com.tao.auth.core.domain.User;
import com.tao.auth.core.global.reference.SpringContextHolder;
import com.tao.auth.core.handler.AuthServerHandler;
import com.tao.auth.core.handler.MsgHandler;
import com.tao.auth.core.service.LoginService;
import com.tao.auth.core.utils.RouteUtils;
import com.tao.common.security.Md5Util;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.message.client2server.auth.Auth;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michael on 2017/7/5.
 */

/**
 * 处理CLogin消息的handler.
 * 负责处理用户登陆消息.
 */
public class CLoginHandler extends MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(CLoginHandler.class);

    private LoginService loginService;


    public CLoginHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
        this.loginService = SpringContextHolder.getBean("loginService");
    }


    @Override
    public void excute(Worker worker) throws Exception {

        //获取CLogin对象
        Auth.CLogin cLogin = (Auth.CLogin) msg;

        String userId = cLogin.getUserId();
        String password = cLogin.getPassword();

        /**
         * (1) 判断用户是否存在
         * (2) 如果用户存在,判断密码(md5加密)是否正确
         */
        User user = loginService.getUserByUserId(userId);
        if(user == null) {
            //用户不存在
            RouteUtils.sendResponse(Common.ACCOUNT_INEXIST,
                    "Account not exists.", netId, userId);
            logger.info("Account not exists. userId = {}", userId);
        } else {
            //账号存在
            //验证密码是否输入正确
            String md5_password = Md5Util.md5(password);
            if(userId.equals(user.getUserId()) && md5_password.equals(user.getPassword())) {
                //密码验证成功，说明登陆成功
                //向loginMap中缓存登陆的用户
                AuthServerHandler.putUserIdAndNetId(userId, netId);
                RouteUtils.sendResponse(Common.LOGIN_SUCCESS,
                        "Login success.", netId, userId);
                logger.info("Login success. userId = {}", userId);
                return;
            } else {
                //验证失败
                RouteUtils.sendResponse(Common.LOGIN_ERROR,
                        "Login failed, password is wrong.", netId, userId);
                logger.info("Login failed, password is wrong. userId = {}", userId);
                return;
            }
        }

    }
}










