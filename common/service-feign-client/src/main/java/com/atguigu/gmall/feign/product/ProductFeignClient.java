package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryAndChildTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("service-product")
@RequestMapping("/rpc/inner/product")
public interface ProductFeignClient {


    /**
     * 获取系统的所有分类以及子分类
     * @return
     */
    @ResponseBody
    @GetMapping("/categorys")
    Result<List<CategoryAndChildTo>> getAllCategoryWithChilds();


    /**
     * 获取一个sku的分类层级信息
     * @param skuId
     * @return
     */
    @GetMapping("/category/view/{skuId}")
    Result<BaseCategoryView> getSkuCategoryView(
            @PathVariable("skuId") Long skuId);

    /**
     * 查询skuinfo信息
     * @param skuId
     * @return
     */
    @GetMapping("/skuinfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查询商品价格
     * @param skuId
     * @return
     */
    @GetMapping("/skuinfo/price/{skuId}")
    Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId);

    /**
     * 查询指定的sku对应的spu对应的销售属性名和值
     */
    @GetMapping("/skuinfo/spu/saleattrandvalues/{skuId}")
    Result<List<SpuSaleAttr>> getSkudeSpuSaleAttrAndValue(@PathVariable("skuId") Long skuId);


    /**
     * 查询skuValueJson数据
     * {
     * “118|120”:”49”,
     * ”119|120”:”50”
     *  }
     */
    @GetMapping("/skuinfo/valuejson/{skuId}")
    Result<Map<String,String>> getSkuValueJson(@PathVariable("skuId") Long skuId);

}
