package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {


    /**
     * 批量更新购物车中商品价格
     * @param cartKey
     * @param cartItems
     */
    void updatePriceBatch(String cartKey);
    /**
     * 更新指定购物车中某个商品的价格
     * @param cartKey
     * @param skuId
     * @param price
     */
    void updateCartItemPrice(String cartKey, Long skuId, BigDecimal price);
    /**
     * 设置过期时间
     * @param cartKey
     */
    void  setCartTimeout(String cartKey);
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

    /**
     * 修改购物车中某个商品数量
     * @param skuId
     * @param num
     */
    void updateCartItemNum(Long skuId, Integer num);

    /**
     * 修改购物车中某个商品的选中状态
     * @param skuid
     * @param checked
     */
    void updateCartItemCheckedStatus(Long skuid, Integer checked);

    void deleteCartItem(Long skuid);

    /**
     * 删除购物车中选中的商品
     */
    void deleteChecked();

    /**
     * 删除整个购物车
     * @param cartKey
     */
    void deleteCart(String cartKey);


    /**
     * 判断这个购物车是否溢出
     * @param cartKey
     * @return
     */
    void validateCartOverflow(String cartKey);

}
