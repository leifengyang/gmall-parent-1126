package com.atguigu.gmall.seckill.config;


import com.atguigu.gmall.annotation.EnableAutoHandleException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;





@EnableScheduling   //数据库版
@EnableTransactionManagement
@EnableAutoHandleException //全局自动异常处理
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@EnableRabbit
@MapperScan(basePackages = "com.atguigu.gmall.seckill.mapper")
@Configuration
public class AppConfig {
}
