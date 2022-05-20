package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author lfy
* @description 针对表【spu_sale_attr_value(spu销售属性值)】的数据库操作Mapper
* @createDate 2022-05-20 09:11:19
* @Entity com.atguigu.gmall.product.domain.SpuSaleAttrValue
*/
public interface SpuSaleAttrValueMapper extends BaseMapper<SpuSaleAttrValue> {

    /**
     * 查询spuId对应的所有销售属性名和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrAndValue(@Param("spuId") Long spuId);
}




