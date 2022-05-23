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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {


    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor corePool;
    //商品详情服务：
    //查询sku详情得做这么多式
    //1、查分类     1s
    //2、查Sku信息  200ms
    //3、查sku的图片列表 1s
    //4、查价格    30ms
    //5、查所有销售属性组合  100ms
    //6、查实际sku组合  500ms
    //7、查介绍(不用管)
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
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
        CompletableFuture<Void> skuInfoTask =  CompletableFuture.runAsync(()->{
            Result<SkuInfo> skuInfoResult = productFeignClient.getSkuInfo(skuId);
            if(skuInfoResult.isOk()){
                SkuInfo skuInfo = skuInfoResult.getData();
                detail.setSkuInfo(skuInfoResult.getData());
            }
        },corePool);




        //4、查价格
        CompletableFuture<Void> priceTask =  CompletableFuture.runAsync(()->{
            Result<BigDecimal> skuPriceResult = productFeignClient.getSkuPrice(skuId);
            if(skuPriceResult.isOk()){
                detail.setPrice(skuPriceResult.getData());
            }
        },corePool);



        //5、查所有销售属性组合
        CompletableFuture<Void> saleAttrTask = CompletableFuture.runAsync(()->{
            Result<List<SpuSaleAttr>> saleAttrAndValue = productFeignClient.getSkudeSpuSaleAttrAndValue(skuId);
            if(saleAttrAndValue.isOk()){
                detail.setSpuSaleAttrList(saleAttrAndValue.getData());
            }
        },corePool);


        //6、查询ValueJson
        CompletableFuture<Void> valueJsonTask = CompletableFuture.runAsync(()->{
            Result<Map<String, String>> skuValueJson = productFeignClient.getSkuValueJson(skuId);
            if(skuValueJson.isOk()){
                Map<String, String> jsonData = skuValueJson.getData();
                detail.setValuesSkuJson(JSONs.toStr(jsonData));
            }
        },corePool);


        CompletableFuture.allOf(categoryTask,skuInfoTask,priceTask,saleAttrTask,valueJsonTask)
                .join(); //allOf 返回的 CompletableFuture 总任务结束再往下

        return detail;
    }
}
