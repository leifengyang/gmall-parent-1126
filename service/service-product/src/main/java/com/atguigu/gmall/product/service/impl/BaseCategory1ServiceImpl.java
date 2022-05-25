package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.starter.cache.aop.annotation.Cache;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildTo;
import com.atguigu.gmall.product.dao.BaseCategory1Dao;
import com.atguigu.gmall.product.dao.BaseCategory2Dao;
import com.atguigu.gmall.product.dao.BaseCategory3Dao;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.atguigu.gmall.starter.cache.service.CacheService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BaseCategory1ServiceImpl implements BaseCategoryService {

    @Resource
    BaseCategory1Dao baseCategory1Dao;

    @Autowired
    BaseCategory2Dao baseCategory2Dao;

    @Autowired
    BaseCategory3Dao baseCategory3Dao;


    @Autowired
    CacheService cacheService;



    /**
     * 缓存
     */
    Map<String,Object> cache = new ConcurrentHashMap<>();

    //#{}代表开始计算
    // args[x]代表获取参数某个值  nowDate：获取当前时间   redis->hello：从redis查出hello的值
    // mysql->hello(1)： 代表从当前数据库的hello表查出id=1的数据

    @Cache(cacheKey = "category:level:1")
    @Override
    public List<BaseCategory1> getAllCategory1() {
        List<BaseCategory1> category1s = baseCategory1Dao.selectList(null);
        return category1s;
    }


    @Cache(cacheKey = "category:level:2:#{#args[0]}")
    @Override
    public List<BaseCategory2> getCategory2ByC1id(Long category1Id) {
        //查询某个1级分类的二级分类
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        //构造条件
        wrapper.eq("category1_id",category1Id);

        List<BaseCategory2> category2s = baseCategory2Dao.selectList(wrapper);
        return category2s;
    }

    @Cache(cacheKey = "category:level:3:#{#args[0]}")
    @Override
    public List<BaseCategory3> getCategory3ByC2id(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("category2_id",category2Id);

        List<BaseCategory3> baseCategory3s = baseCategory3Dao.selectList(queryWrapper);
        return baseCategory3s;
    }




    @Cache(cacheKey = RedisConst.CATEGORY_CACHE_KEY)
    @Override
    public List<CategoryAndChildTo> getAllCategoryWithChilds() {
//        //1、查询缓存
//        Object cacheData = cacheService.getCacheData(RedisConst.CATEGORY_CACHE_KEY,
//                new TypeReference<List<CategoryAndChildTo>>() {
//        });
//
//
//        if(cacheData == null){
//            //2、缓存没有查询数据库
//            List<CategoryAndChildTo> childs = baseCategory1Dao.getAllCategoryWithChilds();
//            //3、放入缓存
//            cacheService.save(RedisConst.CATEGORY_CACHE_KEY,childs);
//            return childs;
//        }

        List<CategoryAndChildTo> childs = baseCategory1Dao.getAllCategoryWithChilds();

        //4、返回缓存数据
        return childs;
    }

    @Override
    public BaseCategoryView getSkuCategoryView(Long skuId) {

        BaseCategoryView view = baseCategory1Dao.getSkuCategoryView(skuId);
        return view;
    }
}
