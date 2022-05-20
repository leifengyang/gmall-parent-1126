package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lfy
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-05-20 09:04:03
*/
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * 保存spu信息
     * @param spuInfo  前端提交来的数据
     */
    void saveSpuInfo(SpuInfo spuInfo);
}
