package com.tao.thirdparty.redis;

/**
 * Created by tao on 2017/7/7.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Redis连接池管理器.
 */
public class RedisPoolManager {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisPoolManager.class);

    private String redisServer;     //redis服务器ip地址
    private int redisPort;          //redis服务器监听端口号

    private JedisPool jedisPool = null;


    /**
     * 获得一个JedisPool的实例.
     * @return
     */
    private JedisPool getJedisPool() {

        if(jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(RedisConfig.MAX_TOTAL);
            config.setMaxIdle(RedisConfig.MAX_IDLE);
            config.setMaxWaitMillis(RedisConfig.MAX_WAIT);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);

            jedisPool = new JedisPool(config, redisServer, redisPort, RedisConfig.TIMEOUT);
        }

        return jedisPool;
    }


    /**
     * 获取一个Jedis实例.
     * @return
     */
    public Jedis getJedis() {

        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return jedis;
    }


    public void closeJedis(Jedis jedis) {

        if(jedis != null) {
            jedis.close();
        }
    }

    public String getRedisServer() {
        return redisServer;
    }

    public RedisPoolManager setRedisServer(String redisServer) {
        this.redisServer = redisServer;
        return this;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public RedisPoolManager setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }
}














