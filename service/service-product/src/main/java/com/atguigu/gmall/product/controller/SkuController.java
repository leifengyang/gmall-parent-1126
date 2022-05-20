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
