package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.dto.ValueJsonDto;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author lfy
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Service实现
* @createDate 2022-05-20 09:11:19
*/
@Service
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueMapper, SkuSaleAttrValue>
    implements SkuSaleAttrValueService{

    @Autowired
    SkuSaleAttrValueMapper saleAttrValueMapper;

    @Override
    public Map<String, String> getSkuValueJson(Long skuId) {
        Map<String, String> map = new HashMap<>();

        //去数据库查询我和兄弟们的sku销售属性组合信息
        List<ValueJsonDto> dtos = saleAttrValueMapper.getSkuValueJson(skuId);
        for (ValueJsonDto dto : dtos) {
            map.put(dto.getValueJson(),dto.getId().toString());
        }


        return map;
    }
}




