package com.cesela.gmall.conf;

import com.cesela.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Date:2020/7/27 18:04
 * <p>
 * 写一个 spring 整合 redis 的配置类
 * 将 redis 的链接池创建到 spring 的容器中
 */
@Configuration
public class RedisConfig {

    //读取配置文件中的redis的ip地址
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedisUtil getRedisUtil() {
        if (host.equals("disabled")) {
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initPool(host, port, database);
        return redisUtil;
    }

}

