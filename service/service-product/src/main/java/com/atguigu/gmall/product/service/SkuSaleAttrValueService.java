package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author lfy
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Service
* @createDate 2022-05-20 09:11:19
*/
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {


    /**
     * 查询当前sku以及兄弟们的所有销售属性组合
     * @param skuId
     * @return
     */
    Map<String, String> getSkuValueJson(Long skuId);
}
