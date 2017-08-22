package com.tao.common.heartbeat;

/**
 * Created by michael on 17-8-22.
 */

/**
 * 信条消息的类型
 */
public enum MsgType {

    Ping(1),
    Pong(2);

    private int value;

    private MsgType(int value) {
        this.value = value;
    }
}
