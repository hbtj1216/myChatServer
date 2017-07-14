package com.tao.logic.core.component;

/**
 * Created by michael on 17-7-3.
 */

import com.tao.logic.LogicStarter;
import com.tao.logic.core.handler.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 逻辑处理中心.
 * 负责处理各种聊天消息.
 */
public class LogicCenter {

    private static final Logger logger = LoggerFactory.getLogger(LogicCenter.class);

    private Worker[] workers;   //worker数组, 每一个worker代表一个"作业线程"
    private int workerNum;      //worker线程的数量



    public LogicCenter() {

        //初始化workers
        this.workerNum = LogicStarter.workerNum;
        logger.info("LogicCenter create success.");
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

        //注意：和0x7FFFFFFF与, 是为了保证最后的结果为正值
        return (userId.hashCode() & 0x7FFFFFFF) % this.workerNum;
    }

}

























