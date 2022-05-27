package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/product")
@RestController
public class SkuController {


    @Autowired
    SkuInfoService skuInfoService;

    //修改sku数据
    /**
     * 1、收请求Controller处理
     * 2、SkuInfoService.updateSkuInfo(){
     *     //2.1、改数据库
     *     //断电
     *     //2.2、改缓存【改不掉】导致数据不一致
     *     //1.缓存的每个数据必须有过期时间。万一数据不一致到了过期时间删除后，下一次的查询一定是最新
     *     //2.数据的不一致时间有点长; 缩短这个时间
     *
     *
     * }
     */


    /**
     * 获取sku分页数据列表
     * @return
     */
    @GetMapping("/list/{page}/{limit}")
    public Result skuList(@PathVariable("page")Long page,
                          @PathVariable("limit")Long limit){
        Page<SkuInfo> skuInfoPage = skuInfoService.page(new Page<SkuInfo>(page, limit));

        return Result.ok(skuInfoPage);
    }


    /**
     * sku信息保存
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){


        skuInfoService.saveSkuInfo(skuInfo);

        return Result.ok();
    }

    //admin/product/

    /**
     * 上架
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){

        skuInfoService.upOrDownSku(skuId,1);

        return Result.ok();
    }


    /**
     * 下架
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.upOrDownSku(skuId,0);
        return Result.ok();
    }
}
