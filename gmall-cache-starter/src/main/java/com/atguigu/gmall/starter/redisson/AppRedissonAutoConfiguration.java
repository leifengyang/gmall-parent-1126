package com.atguigu.gmall.starter.redisson;



import com.atguigu.gmall.starter.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Redisson的自动配置类
 */
@Slf4j
@AutoConfigureAfter(RedisAutoConfiguration.class) //必须在redis自动配置之后再进行
@Configuration
public class AppRedissonAutoConfiguration {


    /**
     * 自动注入了所有的布隆任务，创建布隆过滤器的时候就会自动运行这些任务
     */
    @Autowired(required = false)
    List<BloomTask> bloomTask; //自动找到容器中所有这种类型的组件



    /**
     *
     * @param redisProperties  自动注入redis的配置
     * @return
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties){
        Config config = new Config();
        //Redis url should start with redis:// or rediss:// (for SSL connection)


        config.setLockWatchdogTimeout(30*1000);
        config.useSingleServer()
                .setAddress("redis://"+redisProperties.getHost()+":"+redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                ;


        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }


    /**
     * 注入常用的布隆过滤器
     */
    @Bean
    public RBloomFilter<Object>  skuIdBloom(RedissonClient redissonClient){
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.BLOOM_SKU_ID);

        if (filter.isExists()) {
            //如果存在，代表有这个布隆
            log.info("redis已经配置好了布隆");
            return filter;
        }else {
            log.info("redis还没有这个布隆，正在进行初始化");
            //如果不存在，说明redis没创建过，要初始化好这个布隆
            filter.tryInit(5000000,0.0000001);
            //准备数据
            for (BloomTask task : bloomTask) {
                //只运行自己的布隆任务
                if(task instanceof SkuBloomTask){
                    task.initData(filter);
                }
            }
        }

        return filter;
    }




}
