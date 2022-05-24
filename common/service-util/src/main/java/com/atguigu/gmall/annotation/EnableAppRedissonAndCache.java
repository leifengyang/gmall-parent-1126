package com.atguigu.gmall.annotation;

import com.atguigu.gmall.cache.service.CacheService;
import com.atguigu.gmall.cache.service.impl.CacheServiceImpl;
import com.atguigu.gmall.config.threadpool.AppThreadPoolAutoConfiguration;
import com.atguigu.gmall.redisson.AppRedissonAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Import({AppRedissonAutoConfiguration.class, CacheServiceImpl.class})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAppRedissonAndCache {
}
