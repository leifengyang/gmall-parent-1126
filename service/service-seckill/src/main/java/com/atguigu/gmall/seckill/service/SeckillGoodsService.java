package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author lfy
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-06-11 09:03:12
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {


    /**
     * 查询当天的所有秒杀商品
     * 1、去redis查。如果redis没有，继续查库，查到以后放redis即可
     * @return
     */
    List<SeckillGoods> queryCurrentDaySeckillGoods();


    List<SeckillGoods> querySpecDaySeckillGoods(String upDays);

    /**
     * 获取指定的秒杀商品的详情
     * @param skuId
     * @return
     */
    SeckillGoods getSeckillGoodsDetail(Long skuId);

    /**
     *
     * @param skuId
     */
    void deduceGoodsStock(Long skuId);
}
