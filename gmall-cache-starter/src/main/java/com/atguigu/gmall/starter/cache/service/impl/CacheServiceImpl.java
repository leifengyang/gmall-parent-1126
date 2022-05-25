package com.atguigu.gmall.starter.cache.service.impl;

import com.atguigu.gmall.starter.cache.service.CacheService;
import com.atguigu.gmall.starter.utils.JSONs;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;


//    @Override
//    public List<CategoryAndChildTo> getCatagorys() {
//        //1、远程查询redis的categorys数据
//        String categorys = redisTemplate.opsForValue().get("categorys");
//
//        //2、redis没有缓存这个key的数据
//        if(StringUtils.isEmpty(categorys)){
//            return null;
//        }
//        //3、redis 有数据 【反序列化】
//        // 对象转流（字符流、字节流）： 序列化
//        // 流转对象：                反序列化
//        // 牵扯数据的传输或者保存。
//        List<CategoryAndChildTo> data = JSONs.strToCategoryObj(categorys);
//        return data;
//    }
//
//    @Override
//    public void saveCategoryData(List<CategoryAndChildTo> childs) {
//        String toStr = JSONs.toStr(childs);
//        redisTemplate.opsForValue().set("categorys",toStr);
//
//    }


    /**
     * 缓存的数据有三种情况
     * 1、真没：
     *      get(key) == null;
     * 2、有：
     *      get(key) == "no"
     *      get(key) == jsonStr
     *
     * @param key
     * @param typeReference
     * @param <T>
     * @return
     */
    @Override
    public <T extends Object> T getCacheData(String key, TypeReference<T> typeReference) {
        //1、获取redis指定key的数据
        String json = redisTemplate.opsForValue().get(key);

        //2、判断。缓存只要这个数据被查过一次，就一定有东西
        if(!StringUtils.isEmpty(json)){
            //3、转换成指定的格式
            if("no".equals(json)){
                T t = JSONs.nullInstance(typeReference);
                return t;
            }

            //4、真实数据
            T t = JSONs.strToObj(json,typeReference);
            return t;
        }


        //4、缓存中真没有，连人都从未查过
        return null; //只要返回null就调用数据库逻辑
    }


    //给缓存中保存数据，要给一个（业务过期+随机过期）时间
    @Override
    public void save(String key, Object data) {
        if(data == null){
            //数据库是 null long timeout, TimeUnit unit //被动型检查数据，缓存的短一点
            redisTemplate.opsForValue().set(key,"no",30, TimeUnit.MINUTES);
        }else {
            //数据库有。 有的数据缓存的久一点

            //为了防止同时过期。给每个过期时间加上随机值
            // 885493875.9834759833754739583948
            Double v = Math.random() * 1000000000L;
            long mill = 1000 * 60 * 60 * 24 *3 + v.intValue();

            redisTemplate.opsForValue().set(key,JSONs.toStr(data),mill,TimeUnit.MILLISECONDS);
        }

    }

}
