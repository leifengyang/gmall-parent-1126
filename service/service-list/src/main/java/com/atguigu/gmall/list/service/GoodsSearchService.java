package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;

public interface GoodsSearchService {

    //保存商品数据到es中
    void saveGoods(Goods goods);

    //删除商品
    void deleteGoods(Long skuId);

    /**
     * 检索
     * @param param
     * @return
     */
    GoodsSearchResultVo search(SearchParam param);

    void updateHotScore(Long skuId, Long score);
}
