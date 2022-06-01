package com.atguigu.gmall.user.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@MapperScan(basePackages = "com.atguigu.gmall.user.mapper")
@EnableTransactionManagement
@Configuration
public class AppConfig {

    public void hha(){
//        Proxy.newProxyInstance(null, null, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
////                method.invoke()
//                return null;
//            }
//        });
    }
}
