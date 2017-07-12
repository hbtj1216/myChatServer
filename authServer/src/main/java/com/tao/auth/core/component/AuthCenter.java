package com.tao.auth.core.component;

/**
 * Created by michael on 17-7-3.
 */

import com.tao.auth.AuthStarter;
import com.tao.auth.core.handler.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证中心.
 * 负责处理各种认证消息.
 */
public class AuthCenter {

    private static final Logger logger = LoggerFactory.getLogger(AuthCenter.class);

    private Worker[] workers;   //worker数组, 每一个worker代表一个"作业线程"

    private int workerNum;  //worker线程的数量



    public AuthCenter() {

        //初始化workers
        this.workerNum = AuthStarter.workerNum;
        logger.info("AuthCenter create success.");
        logger.info("The workerNum : {}", workerNum);
    }


    /**
     * 启动工作线程
     */
    public void startWorkers() {

        this.workers = new Worker[workerNum];
        for(int i = 0; i < workerNum; i++) {
            this.workers[i] = new Worker();
            //启动对应的worker线程
            (this.workers[i]).start();
        }
        logger.info("Workers create success.");
    }


    /**
     * 终止所有的工作线程
     */
    public void stopWorkers() {

        for(int i = 0; i < workerNum; i++) {
            //给标志位赋值true
            workers[i].stop = true;
        }
    }



    /**
     * 分发消息给Worker线程进行处理.
     * @param userId -- 用户id
     * @param msgHandler -- 对应消息的msgHandler
     */
    public void distributeWork(String userId, MsgHandler msgHandler) {

        //首先计算workerIndex
        int workerIndex = getWorkerIndex(userId);
        if(msgHandler == null) {
            logger.info("MsgHandler is null.");
            return;
        }
        //找到对应的worker, 添加msgHandler
        Worker worker = this.workers[workerIndex];
        worker.addMsgHandler(msgHandler);
    }


    /**
     * 根据userId计算workerIndex的值.
     * 因为worker的数量是有限的, 为了分配处理用户认证请求的worker, 需要进行简单的转换.
     * @param userId
     * @return
     */
    private int getWorkerIndex(String userId) {

        return userId.hashCode() % this.workerNum;
    }

}

























