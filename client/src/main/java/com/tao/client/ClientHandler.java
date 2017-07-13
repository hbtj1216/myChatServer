package com.tao.client;

import com.google.protobuf.Message;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.utils.ProtobufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by michael on 17-7-12.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);


    private static ChannelHandlerContext chatServerConnCtx; //客户端连接到服务器的ctx
    private static AtomicLong clientNumber = new AtomicLong(1);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        chatServerConnCtx = ctx;

        /*logger.info("3秒后尝试在未注册的情况下登录...");
        Thread.sleep(3000);
        //模拟未注册下的登录
        loginWithUnregister(ctx);*/


        /*logger.info("3秒后注册账号...");
        Thread.sleep(3000);
        registerAccount(ctx, "hbtj1216", "1234567890", "追风少年");*/

        logger.info("3秒后登录已经注册过的账号...");
        Thread.sleep(3000);
        login(ctx, "hbtj1216", "1234567890");


    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        logger.info("Client received Server message : {}", msg.getClass().getSimpleName());

        //对收到的服务器消息进行判断
        if(msg instanceof Auth.SResponse) {

            /**
             * 注册或者登录的返回信息都会以SResponse消息体返回.
             */

            Auth.SResponse sresp = (Auth.SResponse) msg;
            int code  = sresp.getCode();            //获取消息码
            String content = sresp.getContent();    //获取消息的内容
            //对消息码code进行判断
            switch (code) {

                //账号未注册
                case ClientConstant.ACCOUNT_INEXIST:
                    logger.info("账号未注册, content : {}", content);
                    break;

                //登陆成功
                case ClientConstant.LOGIN_SUCCESS:
                    logger.info("登陆成功, content : {}", content);
                    break;

                //账号或者密码错误
                case ClientConstant.LOGIN_ERROR:
                    logger.info("登录失败, content : {}", content);
                    break;

                //账号已被注册
                case ClientConstant.ACCOUNT_ALREADY_EXIST:
                    logger.info("账号已被注册, content : {}", content);
                    break;

                //账号注册成功
                case ClientConstant.REGISTER_OK:
                    logger.info("账号注册成功, content : {}", content);
                    break;

                default:
                    logger.info("Unknow Code, code : {}", code);
            }
        }
    }


    /**
     * 模拟未注册时候的登录.
     */
    private void loginWithUnregister(ChannelHandlerContext ctx) {


        String userId = "545554463";
        String password = "123456";

        Auth.CLogin.Builder lb = Auth.CLogin.newBuilder();
        lb.setUserId(userId);
        lb.setPassword(password);
        //创建CLogin消息体
        Auth.CLogin cLogin = lb.build();

        //向服务器发送登录消息
        ByteBuf sendBuf = ProtobufUtils.pack2Server(cLogin);
        ctx.writeAndFlush(sendBuf);
        logger.info("Client send [CLogin] message, userId = {}", userId);
    }


    /**
     * 登录已经注册的账号
     * @param ctx
     */
    private void login(ChannelHandlerContext ctx, String userId, String password) {

        Auth.CLogin.Builder lb = Auth.CLogin.newBuilder();
        lb.setUserId(userId);
        lb.setPassword(password);
        //创建CLogin消息体
        Auth.CLogin cLogin = lb.build();

        //向服务器发送登录消息
        ByteBuf sendBuf = ProtobufUtils.pack2Server(cLogin);
        ctx.writeAndFlush(sendBuf);
        logger.info("Client send [CLogin] message, userId = {}", userId);
    }


    /**
     * 模拟注册账号
     * @param ctx
     */
    private void registerAccount(ChannelHandlerContext ctx, String userId, String password, String nickName) {

        Auth.CRegister.Builder crb = Auth.CRegister.newBuilder();
        crb.setUserId(userId);
        crb.setPassword(password);
        crb.setNickName(nickName);
        //创建cRegister消息体
        Auth.CRegister cRegister = crb.build();

        //向服务器发送注册消息
        ByteBuf sendBuf = ProtobufUtils.pack2Server(cRegister);
        ctx.writeAndFlush(sendBuf);
        logger.info("Client send [CRegister] message to Server, userId = {}", userId);

    }

}









