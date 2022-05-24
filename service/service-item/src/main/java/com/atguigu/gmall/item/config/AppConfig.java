package com.atguigu.gmall.item.config;


import com.atguigu.gmall.annotation.EnableAppDoubleThreadPool;
import com.atguigu.gmall.annotation.EnableAppRedissonAndCache;
import com.atguigu.gmall.config.threadpool.AppThreadPoolAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


//每个微服务中，自己需要调用谁就导入谁

@EnableAppRedissonAndCache
@EnableAppDoubleThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@Configuration
public class AppConfig {


}
