package com.atguigu.gmall.list.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rpc/inner/es")
@RestController
public class GoodsRpcController {



    @Autowired
    GoodsSearchService goodsSearchService;

    /**
     * 上架商品，保存数据到ES
     * @param goods
     * @return
     */
    @PostMapping("/goods/save")
    public Result saveGoods(@RequestBody Goods goods){

        goodsSearchService.saveGoods(goods);

        return Result.ok();
    }

    /**
     * 下架商品，从ES中删除数据
     */
    @GetMapping("/goods/delete/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId){
        goodsSearchService.deleteGoods(skuId);
        return Result.ok();
    }


    @PostMapping("/goods/search")
    public Result<GoodsSearchResultVo> searchGoods(@RequestBody SearchParam param){

        //检索
        GoodsSearchResultVo vo = goodsSearchService.search(param);

        return Result.ok(vo);
    }
}
