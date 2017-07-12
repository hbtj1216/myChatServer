package com.tao.thirdparty.redis;

/**
 * Created by tao on 2017/7/7.
 */

/**
 * Redis配置类.
 */
public class RedisConfig {

    private RedisConfig() {}

    /**
     * Redis的最大连接实例数目, 默认值为8.
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽).
     */
    public static int MAX_TOTAL = 1024;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8.
     */
    public static int MAX_IDLE = 20;

    /**
     * 等待可用连接的最大时间, 单位：毫秒.
     * 默认值为-1，表示永不超时.
     */
    public static int MAX_WAIT = 10000;

    /**
     * 超时时间.
     */
    public static int TIMEOUT = 10000;

    /**
     * 重连最大次数.
     */
    public static int RETRY_NUM = 5;
}









