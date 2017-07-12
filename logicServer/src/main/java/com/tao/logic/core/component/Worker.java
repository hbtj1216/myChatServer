package com.tao.logic.core.component;

/**
 * Created by michael on 17-7-3.
 */


import com.tao.logic.core.handler.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 每一个Worker代表一个作业线程.
 * 每个worker有自己的阻塞队列.
 */
public class Worker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    
    public volatile boolean stop = false;   //是否停止的标记

    /**
     * 保存MsgHandler并排队的阻塞队列.
     * 每个worker作业线程都有一个阻塞队列.
     */
    private final BlockingQueue<MsgHandler> msgHandlersQueue = new LinkedBlockingDeque<>();


    /**
     * 每个worker在单独的线程里跑自己的run方法.
     */
    @Override
    public void run() {

        while(!stop) {
            MsgHandler msgHandler = null;
            try {
                //获取消息队列队头元素, 超时时间500ms
                msgHandler = msgHandlersQueue.poll(500, TimeUnit.MILLISECONDS);
                if(msgHandler == null) {
                    //如果msgHandler为null, 说明阻塞队列为空
                    continue;
                }
            } catch (InterruptedException e) {
                logger.error("[LogicServer] Worker Caught Exception.");
            }

            //msgHandler不为null
            //获取redis, 处理
            //TODO


            try {
                //执行msgHandler的excute方法，处理消息.
                msgHandler.excute(this);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


    /**
     * 向worker中的任务队列中添加一个MsgHandler.
     * @param msgHandler
     */
    public void addMsgHandler(MsgHandler msgHandler) {

        if(msgHandler != null) {
            //添加一个msgHandler到任务队列(阻塞队列)中
            this.msgHandlersQueue.offer(msgHandler);
        }
    }

}






















