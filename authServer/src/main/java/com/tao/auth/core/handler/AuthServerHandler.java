package com.tao.auth.core.handler;

/**
 * Created by michael on 17-7-3.
 */

import com.google.protobuf.Message;
import com.tao.auth.core.component.AuthCenter;
import com.tao.auth.core.global.HandlerManager;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.message.internal.Internal;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthServer的消息处理器.
 * 每一个连接对应一个handler.
 * 注意：AuthServer只保持和GateServer的连接.
 */

public class AuthServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(AuthServerHandler.class);

    private AuthCenter authCenter;  //认证中心

//    /**
//     * 登录的用户的缓存.
//     * loginMap保存所有通过login认证的userId以及对应的netId.
//     */
//    private static Map<String, Long> loginMap = new HashMap<>(); //userId到netId的映射map


    private static ChannelHandlerContext gateConnCtx;  //gate连接的ctx



    public AuthServerHandler(AuthCenter authCenter) {
        this.authCenter = authCenter;
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //logger.info("[AuthServer] received connection from [GateServer].");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        logger.info("The connection with gateServer is inactive.");
    }




    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        //authServer收到gateServer发来的消息, 进行处理
        Internal.GTransfer gt = (Internal.GTransfer) msg;
        //获取ptoNum
        int ptoNum = gt.getPtoNum();
        //根据对应的ptoNum获取对应类型的message
        Message message = ParseMap.getMessage(ptoNum, gt.getMsg().toByteArray());

        //消息处理器
        MsgHandler msgHandler = null;

        //如果是Greet类型的消息
        if(message instanceof Internal.Greet) {

            logger.info("[AuthServer] received [Greet] message from [GateServer].");
            //处理Greet请求
            msgHandler = HandlerManager.getMsgHandler(ptoNum, gt.getUserId(), gt.getNetId(), message, ctx);
        } else {
            logger.info("[AuthServer] received [{}] message from [GateServer].", message.getClass().getSimpleName());
            //处理认证请求
            msgHandler = HandlerManager.getMsgHandler(ptoNum, gt.getUserId(), gt.getNetId(), message, getGateConnCtx());
        }

        //将消息分发给AuthCenter中的Worker线程进行处理
        authCenter.distributeWork(gt.getUserId(), msgHandler);

    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        throw new RuntimeException("[AuthServerHandler] caught an exception. ", cause);
    }




//    /**
//     * 向缓存中注册登录成功的用户.
//     * 添加userId到nwetId的映射.
//     * @param userId
//     * @param netId
//     */
//    public static void registerLoginUser(String userId, Long netId) {
//
//        loginMap.put(userId, netId);
//    }
//
//
//    /**
//     * 从缓存中删除注销的用户.
//     * @param userId
//     */
//    public static void unregisterLoginUser(String userId) {
//
//        loginMap.remove(userId);
//    }
//
//
//    /**
//     * 判断用户是否已经登录.
//     * @param userId
//     * @return
//     */
//    public static boolean isUserLogin(String userId) {
//
//        Long netId = loginMap.get(userId);
//        if(netId == null) {
//            //不存在
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    /**
//     * 通过userId获得netId.
//     * @param userId
//     * @return
//     */
//    public static Long getNetIdByUserId(String userId) {
//        Long netId = loginMap.get(userId);
//        if(netId != null) {
//            return netId;
//        } else {
//            return null;
//        }
//    }


    public static ChannelHandlerContext getGateConnCtx() {
        return AuthServerHandler.gateConnCtx;
    }

    public static void setGateConnCtx(ChannelHandlerContext gateConnCtx) {
        AuthServerHandler.gateConnCtx = gateConnCtx;
    }
}







