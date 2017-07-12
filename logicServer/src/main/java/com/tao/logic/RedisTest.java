package com.tao.logic;

import com.tao.thirdparty.redis.RedisPoolManager;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by tao on 2017/7/7.
 */
public class RedisTest {

    private static final String ipAddr = "192.168.208.128";
    private static final int port = 6379;
    private static Jedis jedis = null;


    public static void main(String[] args) {

        RedisPoolManager redisPoolManager = new RedisPoolManager();
        redisPoolManager.setRedisServer(ipAddr)
                .setRedisPort(port);

        Jedis jedis = redisPoolManager.getJedis();

        jedis.set("userName", "zzh");

        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
    }
}
