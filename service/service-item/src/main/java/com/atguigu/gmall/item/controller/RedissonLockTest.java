package com.atguigu.gmall.item.controller;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
public class RedissonLockTest {

    @Autowired
    RedissonClient redissonClient;

    /**
     * 可重入锁特性
     * lock.lock();
     * 1、防死锁：锁默认30s。
     * 2、锁续期：只要业务超长，就会自动续期。每隔1/3的锁时间，会自动续满期
     *      小心： 只要我们自己传了锁的自动释放时间，就会取消自动续期功能
     *
     * 3、锁原子性保证；
     *
     *
     * 小结：
     * lock.lock(); //有自动续期功能
     * lock.tryLock();//有续期功能
     *
     * lock.tryLock(5,10,S);//只要指定了释放时间就不会续期
     *
     *
     * 锁续期源码：
     *
     *
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/lock/hello")
    public String reentrantLock(HttpServletRequest request) throws InterruptedException {
        //1、得到锁
        RLock lock = redissonClient.getLock("hello-lock");
        String remoteAddr = request.getRemoteAddr();
        System.out.println("有人访问："+remoteAddr);
        //2、加锁
//        lock.lock(); //阻塞式加锁
//        boolean b = lock.tryLock(); //不阻塞，就试一下
//        if(b){
//           //加锁成功

//            //3、解锁
//            lock.unlock(); //
//        }


//        boolean b = lock.tryLock(5,TimeUnit.SECONDS); //等锁时间

        lock.lock(); //一定要等到锁
        System.out.println("哈哈");
        Thread.sleep(1000 * 60);


        //不一定要等到锁
//        boolean b = lock.tryLock(5,10,TimeUnit.SECONDS); //等锁时间、自动释放时间
//        if (b) {
//
//        }
        //有限等待 waitTime时间，等到了锁，锁自动
        //等5s，如果5s内加到锁，这个锁被设置为10s，（10s后自动解锁）


        return "ok";
    }
}
