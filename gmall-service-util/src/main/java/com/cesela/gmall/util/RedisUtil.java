package com.cesela.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private static JedisPool jedisPool;

    //创建JedisPool对象
    public static JedisPool initPool(String host, int port, int database) {
        if (jedisPool == null) {
            //创建jedisPool
            //创建JedisPoolConfig，给config设置连接池的参数，使用config对象创建JedisPool
            JedisPoolConfig config = new JedisPoolConfig();
            //设置最大的线程数，一个线程就是一个Jedis
            config.setMaxTotal(200);
            //设置最大空弦数
            config.setMaxIdle(30);
            config.setBlockWhenExhausted(true);
            config.setMaxWaitMillis(10 * 1000);
            //设置检查项为true,表示从线程池中获取的对象一定是经过检查可用的
            config.setTestOnBorrow(true);
            /**
             * poolConfig:配置器JedisPoolConfig
             * host:redis所在的linux的ip
             * port:redis的端口
             * timeout:连接redis的超时,毫秒值
             * password:redis的访问密码
             */
            jedisPool = new JedisPool(config, host, port, 6000, "123456");
        }
        return jedisPool;

    }
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
    public static void close(){
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}


