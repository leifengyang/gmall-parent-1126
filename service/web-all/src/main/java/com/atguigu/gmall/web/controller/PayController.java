package com.atguigu.gmall.web.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * 支付
 */
@Controller
public class PayController {


    @Autowired
    OrderFeignClient orderFeignClient;
    /**
     * 展示支付页
     * @param orderId
     * @return
     */
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId,
                          Model model){

        //TODO 数据
        // orderInfo（id，totalAmount）
        Result<OrderInfo> info = orderFeignClient.getOrderInfoByUserId(orderId);

        model.addAttribute("orderInfo",info.getData());

        return "payment/pay";
    }

    /**
     * 支付成功页
     * @return
     */
    @GetMapping("/pay/success.html")
    public String paySuccess(@RequestParam("out_trade_no") String out_trade_no){

        //charset=utf-8&out_trade_no=GMALL-1654672462818-3-7185e&method=alipay.trade.page.pay.return&total_amount=1943.00&sign=wlhFhHhjBtLIlyORpmZGCRAuyvfdS1NSQYJDKWoWJCOaCPObpmmHlpLCyo4Isa%2FF3nY%2BMBjHfOT1VLD0tUQkIFZr5G3zyJzh7AmciHdtt1Y6WFF5Ub3s6bg0X8CHHSuZ5R6DgEjr0daF8DtrWy7NjSxiksOuvimQ5Z9nsD5BqlJULjBouiSYkNhSk6Xb4FEaWAyv9bregypjNlGp8Z4rWVRIJ418Eo6whNSyufbL%2BuT8KExe85nqqajrOBp8ODIq4%2FDySLje5exybgqdP%2FaytE9Wx9THJo4lniKVdiV8w%2BFy6elt43Tcsnn%2BNfdUSuJzj3mf6vv%2F9yEoIR3%2BAqPyzA%3D%3D&trade_no=2022060822001435030502016942&auth_app_id=2016092200568607&version=1.0&app_id=2016092200568607&sign_type=RSA2&seller_id=2088102176735660&timestamp=2022-06-08+15%3A19%3A42
        //out_trade_no=GMALL-1654587260378-3-a4b56
        //改订单为已支付。能在这里改不？
        //out_trade_no=GMALL-1654672462818-3-7185e

        //1、查下订单的状态。如果还是 UNPAID ，就可以通知后台自己去改订单
        orderFeignClient.checkOrderStatus(out_trade_no);


        return "payment/success";
    }
}
