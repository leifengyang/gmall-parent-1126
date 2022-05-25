package com.atguigu.gmall.item.config;


import com.atguigu.gmall.annotation.EnableAppDoubleThreadPool;

import com.atguigu.gmall.starter.annotation.EnableAppRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


//每个微服务中，自己需要调用谁就导入谁

//@EnableAppRedissonAndCache
//@EnableAutoCache //开启自动缓存功能


@EnableAspectJAutoProxy //开启切面自动代理功能
@EnableAppDoubleThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@Configuration
public class AppConfig {


}
