package com.atguigu.gmall.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 支付
 */
@Controller
public class PayController {

    //pay.html?orderId=740955934443962368
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId){

        //TODO 数据
        return "payment/pay";
    }
}
