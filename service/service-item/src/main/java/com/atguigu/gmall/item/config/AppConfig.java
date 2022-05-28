package com.atguigu.gmall.item.config;


import com.atguigu.gmall.annotation.EnableAppDoubleThreadPool;

import com.atguigu.gmall.starter.annotation.EnableAppRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


//每个微服务中，自己需要调用谁就导入谁

//@EnableAppRedissonAndCache
//@EnableAutoCache //开启自动缓存功能


@EnableAspectJAutoProxy //开启切面自动代理功能
@EnableAppDoubleThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@Configuration
public class AppConfig  {


//    implements BeanDefinitionRegistryPostProcessor
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//        registry.registerBeanDefinition("zzz",new RootBeanDefinition());
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//
//    }

}
