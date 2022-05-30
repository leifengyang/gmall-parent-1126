package com.atguigu.gmall.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void zsetTest(){
        //加1以后的最新得分
        for (int i = 0; i < 78; i++) {
            Double score = redisTemplate.opsForZSet().incrementScore("sku:hotscore", "49", 1.0);
            System.out.println(score);
        }

    }
}
