package com.atguigu.gmall.product.config;


import com.atguigu.gmall.annotation.*;
import com.atguigu.gmall.config.AppMybatisPlusConfiguratoin;
import com.atguigu.gmall.starter.annotation.EnableAppRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

/**
 * 当前应用的配置
 */
//@ProductConfig
@MapperScan(basePackages = {"com.atguigu.gmall.product.dao","com.atguigu.gmall.product.mapper"})
@Import(AppMybatisPlusConfiguratoin.class)
@EnableMinio
@EnableAutoHandleException
@EnableAppSwaggerApi
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.list")
@EnableScheduling
@Configuration
public class AppConfiguration {




}
