package com.tao.auth.core.handler.messageHandler;

/**
 * Created by Michael on 2017/7/5.
 */

import com.google.protobuf.Message;
import com.tao.auth.core.component.Worker;
import com.tao.auth.core.domain.User;
import com.tao.auth.core.global.reference.SpringContextHolder;
import com.tao.auth.core.handler.MsgHandler;
import com.tao.auth.core.service.RegisterService;
import com.tao.auth.core.utils.RouteUtils;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.message.client2server.auth.Auth;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理CRegister消息的handler.
 * 负责处理客户端的用户注册消息.
 */
public class CRegisterHandler extends MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(CRegisterHandler.class);

    private RegisterService registerService;

    public CRegisterHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
        //注入registerService
        this.registerService = SpringContextHolder.getBean("registerService");
    }


    @Override
    public void excute(Worker worker) throws Exception {

        //获取CRegister消息
        Auth.CRegister cRegister = (Auth.CRegister) msg;

        //获取注册消息中的信息
        String userId = cRegister.getUserId();
        String password = cRegister.getPassword();
        String nickName = cRegister.getNickName();

        //创建用户实体
        User registerUser = new User(userId, password, nickName);
        logger.info("[新用户请求注册]: " + registerUser);

        //首先判断用户是否已经注册
        if(registerService.exist(registerUser)) {
            //账号已经注册过
            RouteUtils.sendResponse(Common.ACCOUNT_ALREADY_EXIST,
                    "Account already exists.", netId, userId);
            logger.info("Account already exists. userId = {}", userId);
            return;
        } else {
            //注册用户,写入数据库
            registerService.registerUser(registerUser);
            RouteUtils.sendResponse(Common.REGISTER_OK,
                    "Account registerd successd.", netId, userId);
            logger.info("Account registerd successd. userId = {}", userId);
        }
    }
}















