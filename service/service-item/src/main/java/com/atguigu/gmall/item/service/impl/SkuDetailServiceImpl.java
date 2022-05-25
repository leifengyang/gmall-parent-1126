package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.starter.cache.service.CacheService;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.starter.cache.aop.annotation.Cache;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {


    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor corePool;

    @Autowired
    CacheService cacheService;

    @Autowired
    RBloomFilter<Object> skuIdBloom;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    //cacheKey = "sku:detail:#{args[0]}"   计算后 sku:detail:49
    //sku:detail:49  // 0~10000


    // 使用 bloomName 指定的布隆过滤器判定 bloomValue 是否存在
    @Cache(cacheKey = RedisConst.SKU_CACHE_KEY_PREFIX+"#{#args[0]}",
            bloomName = "skuIdBloom",bloomValue="#{#args[0]}")
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        log.info("正在从数据库等确定商品详情：{}",skuId);
        return getSkuDetailFromDb(skuId);
    }

    /**
     * 查询商品详情。使用Redisson提供的分布式锁
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetailWithRedissonLock(Long skuId) {
        String cacheKey = RedisConst.SKU_CACHE_KEY_PREFIX + skuId;
        //1、查询缓存
        SkuDetailTo cacheData = cacheService.getCacheData(cacheKey,
                new TypeReference<SkuDetailTo>() {
                });

        if(cacheData == null){
            log.info("SkuDetail：{}：缓存未命中，准备回源",skuId);
            //2、缓存没有准备回源，布隆通过
            if (skuIdBloom.contains(skuId)){
                log.info("SkuDetail：{}：布隆过滤通过",skuId);
                //5、开始查库，加锁防止击穿
                RLock lock = redissonClient.getLock(RedisConst.SKUDETAIL_LOCK_PREFIX + skuId);
                //6、加锁

                boolean tryLock = false; //自动解锁+自动续期
                try {
                    tryLock = lock.tryLock();
                    //7、加锁成功
                    if(tryLock){
                        log.info("SkuDetail：{}：回源锁加锁成功",skuId);
                        SkuDetailTo detail = getSkuDetailFromDb(skuId);
                        //保存到缓存【防null穿透，防雪崩】
                        cacheService.save(cacheKey,detail);

                        return detail;
                    }
                }finally {
                    try {
                        if(tryLock) lock.unlock();
                    }catch (Exception e){
                        log.error("SkuDetail：又想解别人锁了.... {}",e);
                    }
                }


                //8、加锁失败。等待1s直接查缓存
                log.info("SkuDetail：{}：回源锁加锁失败，1s后直接看缓存即可",skuId);
                try {
                    Thread.sleep(1000);
                    cacheData = cacheService.getCacheData(cacheKey,
                            new TypeReference<SkuDetailTo>() {
                            });
                    //返回
                    return cacheData;
                } catch (InterruptedException e) {
                    log.error("SkuDetail：睡眠异常：{}",e);
                }

            }
            //3、布隆不通过
            log.info("SkuDetai：{}：布隆过滤打回",skuId);
            return null;
        }

        //4、缓存不为null，直接返回
        log.info("SkuDetail：{}：缓存命中",skuId);
        //缓存命中率？越高越好
        // 1-1: 0
        // 2-2： 命中/总请求 = 0.5
        // 3-3:  2/3 = 0.6667
        // N-N;  (n-1)/n = 0.99999999
        return cacheData;
    }



    /**
     * 使用Redis原生的分布式锁。
     * 引入缓存的查询商品详情；
     * key: sku:detail:45   value: json
     * key: sku:detail:46   value: json
     * key: sku:detail:47   value: json
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetailWithRedisDistLock(Long skuId) {
        String cacheKey = RedisConst.SKU_CACHE_KEY_PREFIX + skuId;
        //1、查询缓存
        SkuDetailTo cacheData = cacheService.getCacheData(cacheKey,
                new TypeReference<SkuDetailTo>() {
                });

        //2、判断
        if (cacheData == null) {
            //3、缓存中没有，查库[回源]
            //回源之前，先问下布隆，这个东西有没有。
            if (skuIdBloom.contains(skuId)) {
                //4、布隆中有
                log.info("SkuDetail：{} 缓存没命中，正在回源：", skuId);

                //5、注意加锁，否则可能会被击穿； 最好加分布式锁。 setnx  防止击穿
                //去redis中占坑一个key（如果这个key没有人占，那我们就能占成功） 原子的。因为redis是原子的
                String token = UUID.randomUUID().toString(); //准备当前线程的唯一id，当做锁的值
                //加锁【占坑+自动过期时间(合起来是原子)】
                //锁的粒度:  一定设计更细粒度的锁，来保证并发吞吐能力
                Boolean lock = redisTemplate.opsForValue()
                        .setIfAbsent(RedisConst.LOCK_PREFIX+skuId, token,10,TimeUnit.SECONDS);
                SkuDetailTo db = null;
                if(lock){
                    //就算100w请求，只会有一个人返回true，代表抢锁成功
                    //即使业务断电，由redis自动删锁
                    //非原子的加锁操作
                    //redisTemplate.expire("lock",10, TimeUnit.SECONDS); //自动解锁逻辑

                    //加锁和设置过期时间应该一起完成；
                    try {
                        log.info("分布式加锁成功：SkuDetail：{} 真的查库：", skuId);
                        db = getSkuDetailFromDb(skuId);
                        cacheService.save(cacheKey, db);
                        //业务期间，如果发生了断电风险，会导致finally不执行，解锁失败，导致死锁

                        //防止业务卡住，锁自动过期
                        //TODO 1、锁的续期； 自动续期
//                        Thread thread = new Thread(()->{
//                            Thread.sleep(3000);
//                            redisTemplate.expire(RedisConst.LOCK_PREFIX+skuId,10,TimeUnit.SECONDS);
//                        });
//                        thread.setDaemon(true);


                    }finally {
                        //释放锁
//                        String lockValue = redisTemplate.opsForValue().get("lock");
                        //删锁脚本 if(redis.get(lock) == "ddddddd") then return redis.del(lock) else return 0
                        // 1（删除成功）  0（删除失败）
                        //删锁： 【对比锁值+删除(合起来保证原子性)】
                        String deleteScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        //List<K> keys, Object... args
                        Long result = redisTemplate.execute(new DefaultRedisScript<>(deleteScript,Long.class),
                                Arrays.asList(RedisConst.LOCK_PREFIX+skuId), token);
                        if(result == 1){
                            log.info("我的分布式锁解锁完成.");
                        }else {
                            //别人的锁【说明之前由于业务卡顿，续期失败等原因，锁被自动释放，被别人抢到】
                            log.info("这是别人的锁，我不能删.");
                        }
                    }
                }else {
                    log.info("分布式锁抢锁失败，1s后直接查缓存");
                    //抢锁失败？ !false
                    try {
                        Thread.sleep(1000); //睡 1s 的业务时长
                        //缓存中是什么样就给什么样。不用走抢锁，100w请求也总共就等1s。不要让100w自旋抢锁
                        cacheData = cacheService.getCacheData(cacheKey,
                                new TypeReference<SkuDetailTo>() {
                                });
                        return cacheData;
                    } catch (InterruptedException e) {

                    }
                }

                return db;
            }

            //5、布隆说没有
            log.info("SkuDetail：{} 缓存没命中，bloom防火墙拦截打回：", skuId);
            return null;
        }

        log.info("SkuDetail：{}缓存命中", skuId);
        //6、缓存中有直接返回
        return cacheData;
    }

    //商品详情服务：
    //查询sku详情得做这么多式
    //1、查分类     1s
    //2、查Sku信息  200ms
    //3、查sku的图片列表 1s
    //4、查价格    30ms
    //5、查所有销售属性组合  100ms
    //6、查实际sku组合  500ms
    //7、查介绍(不用管)
//    @Override
    public SkuDetailTo getSkuDetailFromDb(Long skuId) {
        SkuDetailTo detail = new SkuDetailTo();
        //异步
        //编排：编组(管理) +  排列组合（运行）
        CompletableFuture<Void> categoryTask = CompletableFuture.runAsync(() -> {
            //1、查分类
            Result<BaseCategoryView> skuCategoryViewResult = productFeignClient.getSkuCategoryView(skuId);
            if (skuCategoryViewResult.isOk()) {
                detail.setCategoryView(skuCategoryViewResult.getData());
            }
        }, corePool);


        //2、查Sku信息 & 3、查sku的图片列表
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            Result<SkuInfo> skuInfoResult = productFeignClient.getSkuInfo(skuId);
            if (skuInfoResult.isOk()) {
                SkuInfo skuInfo = skuInfoResult.getData();
                detail.setSkuInfo(skuInfoResult.getData());
            }
        }, corePool);


        //4、查价格
        CompletableFuture<Void> priceTask = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> skuPriceResult = productFeignClient.getSkuPrice(skuId);
            if (skuPriceResult.isOk()) {
                detail.setPrice(skuPriceResult.getData());
            }
        }, corePool);


        //5、查所有销售属性组合
        CompletableFuture<Void> saleAttrTask = CompletableFuture.runAsync(() -> {
            Result<List<SpuSaleAttr>> saleAttrAndValue = productFeignClient.getSkudeSpuSaleAttrAndValue(skuId);
            if (saleAttrAndValue.isOk()) {
                detail.setSpuSaleAttrList(saleAttrAndValue.getData());
            }
        }, corePool);


        //6、查询ValueJson
        CompletableFuture<Void> valueJsonTask = CompletableFuture.runAsync(() -> {
            Result<Map<String, String>> skuValueJson = productFeignClient.getSkuValueJson(skuId);
            if (skuValueJson.isOk()) {
                Map<String, String> jsonData = skuValueJson.getData();
                detail.setValuesSkuJson(JSONs.toStr(jsonData));
            }
        }, corePool);


        CompletableFuture.allOf(categoryTask, skuInfoTask, priceTask, saleAttrTask, valueJsonTask)
                .join(); //allOf 返回的 CompletableFuture 总任务结束再往下

        return detail;
    }



}
