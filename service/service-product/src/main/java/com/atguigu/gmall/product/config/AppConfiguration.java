package com.atguigu.gmall.product.config;


import com.atguigu.gmall.annotation.EnableAppRedissonAndCache;
import com.atguigu.gmall.annotation.EnableAppSwaggerApi;
import com.atguigu.gmall.annotation.EnableAutoHandleException;
import com.atguigu.gmall.annotation.EnableMinio;
import com.atguigu.gmall.config.AppMybatisPlusConfiguratoin;
import com.atguigu.gmall.product.annotation.ProductConfig;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 当前应用的配置
 */
//@ProductConfig
@MapperScan(basePackages = {"com.atguigu.gmall.product.dao","com.atguigu.gmall.product.mapper"})
@Import(AppMybatisPlusConfiguratoin.class)
@EnableMinio
@EnableAutoHandleException
@EnableAppSwaggerApi
@EnableAppRedissonAndCache  //既能用到缓存组件，还能用到redissonclient
@EnableScheduling
@Configuration
public class AppConfiguration {








}
