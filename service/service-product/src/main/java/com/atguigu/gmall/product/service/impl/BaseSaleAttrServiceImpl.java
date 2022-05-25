package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.starter.cache.aop.annotation.Cache;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.mapper.BaseSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author lfy
* @description 针对表【base_sale_attr(基本销售属性表)】的数据库操作Service实现
* @createDate 2022-05-20 09:11:18
*/
@Service
public class BaseSaleAttrServiceImpl extends ServiceImpl<BaseSaleAttrMapper, BaseSaleAttr>
    implements BaseSaleAttrService{



    @Autowired
    SpuSaleAttrValueMapper saleAttrValueMapper;


    //sale:attr:20
    @Cache(cacheKey = RedisConst.SALE_ATTR_CACHE_KEY)
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrAndValue(Long spuId) {

        List<SpuSaleAttr> spuSaleAttrs = saleAttrValueMapper.getSpuSaleAttrAndValue(spuId);
        return spuSaleAttrs;
    }


    //相当于只是一个前缀，未来怎么拼成一个缓存键都不确定
    //自定义表达式
    @Cache(cacheKey = "#{'person:attrs:'+args[1]}") //计算后 person:attrs:zhangsan  person:attrs:lisi
    public SpuSaleAttr heloidjalkjdalksjd(Long id,String name,Integer age,BaseSaleAttr baseSaleAttr){

        return null;
    }
}




