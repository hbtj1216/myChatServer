package com.tao.client;

import com.google.protobuf.Message;
import com.tao.protobuf.constant.Common;
import com.tao.protobuf.message.client2server.auth.Auth;
import com.tao.protobuf.message.client2server.chat.Chat;
import com.tao.protobuf.utils.ClientProtoUtils;
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


    private String selfId = "";    //该客户端登录之后的userId
    private boolean firstIn = true; //第一次进入
    public volatile boolean login_success = false;  //登录是否成功的标记


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

        /*logger.info("3秒后登录已经注册过的账号...");
        Thread.sleep(3000);
        login(ctx, "hbtj1216", "1234567890");*/


        /**
         * 模拟未登录的时候发送聊天消息
         */
        /*sendMessage(ctx, selfId, "545554463", "哈喽, 大家好啊!");*/

        /**
         * 先登录，然后发消息
         */
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
                case Common.ACCOUNT_INEXIST:
                    logger.info("账号未注册, content : {}", content);
                    break;

                //登陆成功
                case Common.LOGIN_SUCCESS:
                    logger.info("登陆成功, content : {}", content);
                    login_success = true;
                    break;

                //账号或者密码错误
                case Common.LOGIN_ERROR:
                    logger.info("登录失败, content : {}", content);
                    break;

                //账号已被注册
                case Common.ACCOUNT_ALREADY_EXIST:
                    logger.info("账号已被注册, content : {}", content);
                    break;

                //账号注册成功
                case Common.REGISTER_OK:
                    logger.info("账号注册成功, content : {}", content);
                    break;

                //重复登录
                case Common.REPEATED_LOGIN:
                    logger.info("账号已经登录, 不允许重复登录, content : {}", content);
                    break;

                //发送者未登录
                case Common.CHAT_SENDER_OFFLINE:
                    logger.info("账号未登陆, 请登陆后再发送消息. content : {}", content);
                    break;

                //服务器接收消息成功
                case Common.CHAT_MESSAGE_RECEIVE_SUCCESS:
                    logger.info("服务器成功接收到消息! content : {}", content);
                    break;

                //服务器接收消息失败
                case Common.CHAT_MESSAGE_RECEIVE_FAILED:
                    logger.info("服务器没能成功接收到消息! content : {}", content);
                    break;


                default:
                    logger.info("Unknow Code, code : {}", code);
                    break;
            }
        } else if(msg instanceof Chat.CChatMsg) {
            //如果客户端你收到聊天消息
            Chat.CChatMsg cChatMsg = (Chat.CChatMsg) msg;
            String senderId = cChatMsg.getSenderId();       //发送者
            String receiverId = cChatMsg.getReceiverId();   //接收者
            String content = cChatMsg.getContent();         //聊天内容
            System.out.println("客户端收到聊天消息!!!");
            System.out.println("发送者: " + senderId + "    接收者: " + receiverId);
            System.out.println("消息内容: " + content);
        }


        if(firstIn) {

            firstIn = false;

            new Thread(new Runnable() {


                @Override
                public void run() {

                    while(login_success) {

                        logger.info("登录成功, userId: {}, 5秒后发送消息!", selfId);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendMessage(ctx, selfId, selfId, "哈喽, 给自己发的消息哦!");
                        //sendMessage(ctx, selfId, "all", "哈喽, 给所有人发的消息哦!");
                    }
                }
            }).start();
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
        ByteBuf sendBuf = ClientProtoUtils.pack2Server(cLogin);
        ctx.writeAndFlush(sendBuf);
        logger.info("Client send [CLogin] message, userId = {}", userId);
    }


    /**
     * 登录已经注册的账号
     * @param ctx
     */
    private void login(ChannelHandlerContext ctx, String userId, String password) {

        this.selfId = userId;
        Auth.CLogin.Builder lb = Auth.CLogin.newBuilder();
        lb.setUserId(userId);
        lb.setPassword(password);
        //创建CLogin消息体
        Auth.CLogin cLogin = lb.build();

        //向服务器发送登录消息
        ByteBuf sendBuf = ClientProtoUtils.pack2Server(cLogin);
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
        ByteBuf sendBuf = ClientProtoUtils.pack2Server(cRegister);
        ctx.writeAndFlush(sendBuf);
        logger.info("Client send [CRegister] message to Server, userId = {}", userId);

    }


    /**
     * 模拟客户端发送消息
     */
    private void sendMessage(ChannelHandlerContext ctx, String senderId, String receiverId, String content) {

        Chat.CChatMsg.Builder cb = Chat.CChatMsg.newBuilder();
        cb.setSenderId(senderId);           //sender userId
        cb.setReceiverId(receiverId);       //receiver userId
        cb.setContent(content);            //content
        //生成CChatMsg对象
        Chat.CChatMsg cChatMsg = cb.build();

        //发送给服务器
        ByteBuf sendBuf = ClientProtoUtils.pack2Server(cChatMsg);
        ctx.writeAndFlush(sendBuf);
    }

}









