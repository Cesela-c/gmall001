package com.cesela.gmall.seckill.controller;

import com.cesela.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @Date:2020/9/6 11:01
 */
@Controller
public class SecKillController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping(value = "kill")
    @ResponseBody
    public String kill() {
        Jedis jedis = redisUtil.getJedis();
        jedis.watch("106");
        int stock = Integer.parseInt(jedis.get("106"));
        if (stock > 0) {
            Transaction multi = jedis.multi();
            multi.incrBy("106", -1);
            List<Object> exec = multi.exec();
            if (exec != null && exec.size() > 0) {
                System.out.println("当前剩余商品" + stock + "件，某用户抢购成功，抢购人数" + (1000 - stock));
            } else {
                System.out.println("当前库存商品" + stock + "件，某用户抢购失败");
            }

        }
        jedis.close();
        return "1";
    }

    @RequestMapping(value = "secKill")
    @ResponseBody
    public String secKill() {
        Jedis jedis = redisUtil.getJedis();
        RSemaphore semaphore = redissonClient.getSemaphore("106");

        boolean b = semaphore.tryAcquire();
        int stock = Integer.parseInt(jedis.get("106"));
        if (b) {
            System.out.println("当前剩余商品" + stock + "件，某用户抢购成功，抢购人数" + (1000 - stock));
        } else {
            System.out.println("当前库存商品" + stock + "件，某用户抢购失败");
        }
        return "1";
    }
}
