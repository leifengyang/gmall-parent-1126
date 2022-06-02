package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/cart")
@RestController
public class CartController {


    @Autowired
    CartService cartService;
    /**
     * 1、获取购物车列表数据
     * @return
     */
    @GetMapping("/cartList")
    public Result cartList(){
        log.info("获取购物车列表");

        List<CartItem> cartItems = cartService.getCartItems();

        return Result.ok(cartItems);
    }

    //api/cart/addToCart/47/1

    /**
     * 修改购物车中某个商品的数量
     * @param skuId  商品id
     * @param num    数量：  -1 减一个  1 加一个
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("num") Integer num){


        cartService.updateCartItemNum(skuId,num);

        return Result.ok();
    }


    /**
     * 选中购物车中某个商品
     * @param skuid
     * @param checked  0：不选中  1：选中
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{checked}")
    public Result checkCart(@PathVariable("skuId") Long skuid,
                            @PathVariable("checked") Integer checked){

        cartService.updateCartItemCheckedStatus(skuid,checked);
        return Result.ok();
    }


    //api/cart/deleteCart/47
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable("skuId") Long skuid){
        cartService.deleteCartItem(skuid);
        return Result.ok();
    }

}
