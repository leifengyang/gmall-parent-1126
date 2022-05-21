package com.atguigu.gmall.item.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// /rpc/inner/微服务名/功能路径
@RequestMapping("/rpc/inner/item")
@RestController
public class SkuItemDetailRpcController {


    @Autowired
    SkuDetailService skuDetailService;


    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/sku/detail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId){

        SkuDetailTo detailTo = skuDetailService.getSkuDetail(skuId);
        return Result.ok(detailTo);
    }


}