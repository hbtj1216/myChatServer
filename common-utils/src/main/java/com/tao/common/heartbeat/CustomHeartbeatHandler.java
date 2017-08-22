package com.tao.common.heartbeat;

/**
 * Created by michael on 17-8-22.
 */

import com.google.protobuf.Message;
import com.tao.protobuf.analysis.ParseMap;
import com.tao.protobuf.message.othres.heartbeat.Heartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳机制的通用代码.
 * 负责心跳的接收和发送.
 */
public class CustomHeartbeatHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(CustomHeartbeatHandler.class);

    protected String name;
    private int heartbeatCount = 0;


    public CustomHeartbeatHandler(String name) {
        this.name = name;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("===" + ctx.channel().remoteAddress() + " is active.===");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("===" + ctx.channel().remoteAddress() + " is inactive.===");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        //如果是心跳消息
        if (msg instanceof Heartbeat.Heart) {
            Heartbeat.Heart heart = (Heartbeat.Heart) msg;
            //判断是Ping还是Pong
            if(heart.getType() == Heartbeat.MsgType.Ping) {
                //如果收到的是Ping消息, 那么发送Pong消息
                sendPongMsg(ctx);
            } else {
                //如果是Pong消息
                logger.info(name + " 收到 " + ctx.channel().remoteAddress() + " 发来的Pong消息.");
            }
        } else {
            //如果不是心跳消息, 略过, 传递给下一个handler
            ctx.fireChannelRead(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    /**
     * 当产生IdleStateEvent事件的时候调用.
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //查看时三种状态的哪一个
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;

                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;

                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 子类重载
     * @param ctx
     */
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        logger.info("---发生 ALL_IDLE---");
    }

    /**
     * 子类重载
     * @param ctx
     */
    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        logger.info("---发生 WRITER_IDLE---");
    }

    /**
     * 子类重载
     * @param ctx
     */
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        logger.info("---发生 READER_IDLE---");
    }

    /**
     * 向对端发送Ping消息.
     * @param ctx
     */
    protected void sendPingMsg(ChannelHandlerContext ctx) {

        Heartbeat.Heart.Builder hb = Heartbeat.Heart.newBuilder();
        hb.setType(Heartbeat.MsgType.Ping);
        Heartbeat.Heart heart = hb.build(); //心跳消息

        byte[] bytes = heart.toByteArray();
        int length = bytes.length;
        int ptoNum = ParseMap.getPtoNum(heart);

        ByteBuf sendBuf = Unpooled.buffer(8 + length);
        sendBuf.writeInt(length);
        sendBuf.writeInt(ptoNum);
        sendBuf.writeBytes(bytes);

        ctx.channel().writeAndFlush(sendBuf);
        //每主动发一次Ping, 计数器+1
        heartbeatCount++;
        logger.info(name + " 向 " + ctx.channel().remoteAddress() + " 发送了Ping消息, 连续第 " + heartbeatCount + " 次.");

    }


    /**
     * 向对端发送Pong消息
     * @param ctx
     */
    protected void sendPongMsg(ChannelHandlerContext ctx) {

        Heartbeat.Heart.Builder hb = Heartbeat.Heart.newBuilder();
        hb.setType(Heartbeat.MsgType.Pong);
        Heartbeat.Heart heart = hb.build(); //心跳消息

        byte[] bytes = heart.toByteArray();
        int length = bytes.length;
        int ptoNum = ParseMap.getPtoNum(heart);

        ByteBuf sendBuf = Unpooled.buffer(8 + length);
        sendBuf.writeInt(length);
        sendBuf.writeInt(ptoNum);
        sendBuf.writeBytes(bytes);

        ctx.channel().writeAndFlush(sendBuf);
        //每发一次Pong, 计数器+1
        heartbeatCount++;
        logger.info(name + " 向 " + ctx.channel().remoteAddress() + " 发送了Pong消息, 连续第 " + heartbeatCount + " 次.");

    }
}
