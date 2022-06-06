package com.atguigu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


/**
 * 1、导包
 *        <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-validation</artifactId>
 *         </dependency>
 * 2、给vo标注校验注解
 * 3、开启校验;
 *      在所有Controller接受请求参数的时候，使用 @Valid 开启数据校验
 */
@SpringCloudApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
