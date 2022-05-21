package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {


    @Autowired
    ProductFeignClient productFeignClient;
    //商品详情服务：
    //查询sku详情得做这么多式
    //1、查分类
    //2、查Sku信息
    //3、查sku的图片列表
    //4、查价格
    //5、查所有销售属性组合
    //6、查实际sku组合
    //7、查介绍(不用管)
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detail = new SkuDetailTo();
        //1、查分类
        Result<BaseCategoryView> skuCategoryViewResult = productFeignClient.getSkuCategoryView(skuId);
        if(skuCategoryViewResult.isOk()){
            detail.setCategoryView(skuCategoryViewResult.getData());
        }


        //2、查Sku信息 & 3、查sku的图片列表
        Result<SkuInfo> skuInfoResult = productFeignClient.getSkuInfo(skuId);
        if(skuInfoResult.isOk()){
            SkuInfo skuInfo = skuInfoResult.getData();

            detail.setSkuInfo(skuInfoResult.getData());
        }



        //4、查价格
        Result<BigDecimal> skuPriceResult = productFeignClient.getSkuPrice(skuId);
        if(skuPriceResult.isOk()){
            detail.setPrice(skuPriceResult.getData());
        }


        //5、查所有销售属性组合
        Result<List<SpuSaleAttr>> saleAttrAndValue = productFeignClient.getSkudeSpuSaleAttrAndValue(skuId);
        if(saleAttrAndValue.isOk()){
            detail.setSpuSaleAttrList(saleAttrAndValue.getData());
        }

        //6、查询ValueJson
        Result<Map<String, String>> skuValueJson = productFeignClient.getSkuValueJson(skuId);
        if(skuValueJson.isOk()){
            Map<String, String> jsonData = skuValueJson.getData();
            detail.setValuesSkuJson(JSONs.toStr(jsonData));
        }

        return detail;
    }
}
