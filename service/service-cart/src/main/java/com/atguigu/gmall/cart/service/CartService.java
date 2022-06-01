package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartItem;

import java.util.List;

public interface CartService {


    /**
     * 把一个商品添加到购物车
     * @param skuId    商品id
     * @param skuNum   商品数量
     * @return
     */
    CartItem addSkuToCart(Long skuId, Integer skuNum);

    /**
     * 决定用哪个购物车的键
     * @return
     */
    String determinCartKey();


    /**
     * 把商品保存到购物车
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    CartItem  saveSkuToCart(Long skuId,Integer num,String cartKey);

    /**
     * 查询购物车中所有商品
     * @return
     */
    List<CartItem> getCartItems();

}
