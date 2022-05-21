package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
* @author lfy
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-05-20 09:11:19
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1、SkuInfo 的基本信息  保存到 sku_info
        skuInfoMapper.insert(skuInfo);

        //sku的自增id
        Long skuId = skuInfo.getId();

        //2、skuImageList sku图片集合； 存到 sku_image表
        List<SkuImage> imageList = skuInfo.getSkuImageList();
        for (SkuImage image : imageList) {
            image.setSkuId(skuId);
        }
        skuImageService.saveBatch(imageList);

        //3、skuAttrValueList  sku的平台属性  保存到 sku_attr_value。前端提交的就够了
        List<SkuAttrValue> valueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : valueList) {
            attrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(valueList);

        //4、skuSaleAttrValueList sku销售属性值集合 存到 sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue attrValue : skuSaleAttrValueList) {
            attrValue.setSkuId(skuId);
            attrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);


    }

    @Override
    public void upOrDownSku(Long skuId, int status) {
        //1、更数据库状态
        skuInfoMapper.updateSkuStatus(skuId,status);

        //TODO 2、给ES ，保存/删除 数据

    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getPrice(skuId);
    }

    @Override
    public List<SpuSaleAttr> getSkudeSpuSaleAttrAndValue(Long skuId) {

        return spuSaleAttrMapper.getSkudeSpuSaleAttrAndValue(skuId);
    }
}




