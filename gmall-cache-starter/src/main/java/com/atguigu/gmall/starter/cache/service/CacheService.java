package com.atguigu.gmall.starter.cache.service;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CacheService {

    /**
     * 从缓存获取一个数据
     * @param cacheKey
     * @return
     */
    <T>T getCacheData(String cacheKey, TypeReference<T> typeReference);

    /**
     * 给缓存中保存一个数据
     * @param key  缓存数据用的key
     * @param data 缓存的数据
     */
    void save(String key,Object data);
}
