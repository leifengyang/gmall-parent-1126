package com.atguigu.gmall.product.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildTo;

import java.util.List;


/**
 * 对分类的所有操作
 */
public interface BaseCategoryService {
    /**
     * 查询所有1级分类
     * @return
     */
    List<BaseCategory1> getAllCategory1();

    /**
     * 查询某个1级分类的二级分类
     * @param category1Id  一级分类id
     * @return
     */
    List<BaseCategory2> getCategory2ByC1id(Long category1Id);

    /**
     * 查询某个2级分类的所有三级分类
     * @param category2Id 二级分类id
     * @return
     */
    List<BaseCategory3> getCategory3ByC2id(Long category2Id);


    /**
     * 获取所有分类以及子分类数据
     * @return
     */
    List<CategoryAndChildTo> getAllCategoryWithChilds();


    /**
     * 根据skuId获取分类层级路径
     * @param skuId
     * @return
     */
    BaseCategoryView getSkuCategoryView(Long skuId);
}
