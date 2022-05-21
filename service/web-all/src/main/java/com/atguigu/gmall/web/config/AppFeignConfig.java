package com.atguigu.gmall.web.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


/**
 * 1、开启feign功能
 *   @EnableFeignClients
 *   1)、扫描 @EnableFeignClients 所在的类的包以及下面的子包所有的 @FeignClient 标注的组件，创建代理对象并放到容器中
 *   现在：        com.atguigu.gmall.web.config
 *   客户端所在包： com.atguigu.gmall.web.feign
 *
 */
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
@Configuration
public class AppFeignConfig {
}
