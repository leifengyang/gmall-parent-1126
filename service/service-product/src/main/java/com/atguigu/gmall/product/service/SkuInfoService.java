package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lfy
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-05-20 09:11:19
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 保存前端提交的sku数据
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 商品上下架
     * @param skuId
     * @param status
     */
    void upOrDownSku(Long skuId, int status);
}
