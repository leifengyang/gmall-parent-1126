package com.atguigu.gmall.item.controller;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@RestController
public class RessionTestController {

    @Autowired
    StringRedisTemplate redisTemplate;

//    ReentrantLock lock = new ReentrantLock(); //本地锁分布式下不要用


    @Autowired
    RedissonClient redissonClient;


    @GetMapping("/redis/incr")
    public String incrWithRedisson(){
        RLock lock = redissonClient.getLock("lock-abcd");

        try{
            lock.lock();
            //1、获取原值
            String hellocount = redisTemplate.opsForValue().get("hellocount");
            int count = Integer.parseInt(hellocount);

            //2、计算新值
            count++;

            //3、修改原值
            redisTemplate.opsForValue().set("hellocount", count + "");
        }finally {
//            if(lock.isLocked()) lock.unlock();
            try {
                lock.unlock();
            }catch (Exception e){}  //redisson如果发现需要解别人锁，会自动报错，并停止解锁  }
        }



        return "ok";
    }

    /**
     * 压测1w- 涨到1w
     * 1、什么锁都不加： 涨到：232
     * qps/rps: 9746/s
     * 2、本地锁：
     * 启动一份： 【960/s】           是同一把锁；     涨到：10000
     * <p>
     * 启动多份： 负载均衡【1,930/s】  多份不是同一把锁  涨到：4191
     *
     * 3、分布式锁：
     *  直接用setnx：353/s
     *  用redisson：398/s
     *
     * @return
     */
    @GetMapping("/redis/incr/aaa")
    public String incr() {
        System.out.println("处理请求...");

        //第一次抢锁；false
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1", 10, TimeUnit.SECONDS);

        //只要是false就进入while死循环
        while (!lock) {
            //没抢到
            lock = redisTemplate.opsForValue().setIfAbsent("lock", "1", 10, TimeUnit.SECONDS);
        }
        //使用while阻塞住

        //while 是 true就代表加锁成功

        //1、获取原值
        String hellocount = redisTemplate.opsForValue().get("hellocount");
        int count = Integer.parseInt(hellocount);

        //2、计算新值
        count++;


        //3、修改原值
        redisTemplate.opsForValue().set("hellocount", count + "");

        //4、删除锁
        redisTemplate.delete("lock");

        return "ok";
    }
}
