package com.tao.logic.core.handler;

/**
 * Created by michael on 17-7-3.
 */

import com.google.protobuf.Message;

import com.tao.logic.core.component.Worker;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

/**
 * 消息处理器的抽象父类.
 * 所有的消息处理器都应该继承这个父类.
 */
public abstract class MsgHandler {

    protected final String userId;  //用户userId
    protected final long netId;     //userId对应的全局唯一netId
    protected final Message msg;    //被处理的消息
    protected ChannelHandlerContext ctx;    //消息来源的channel上下文(并不是用户channel ctx)


    /**
     * MsgHandler构造函数
     * @param userId
     * @param netId
     * @param msg
     * @param ctx
     */
    protected MsgHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {

        this.userId = userId;
        this.netId = netId;
        this.msg = msg;
        this.ctx = ctx;
    }


    /**
     * 执行消息处理.
     * @param worker
     * @throws Exception
     */
    public abstract void excute(Worker worker) throws Exception;

}










