package com.atguigu.gmall.order.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.ErrVo;
import com.atguigu.gmall.order.vo.ParamValidateTestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网关 凡是带auth路径的必须登录
 */
@RestController
@RequestMapping("/api/order/auth")
public class OrderRestController {


    @Autowired
    OrderService orderService;

    /**
     * 订单提交
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo orderSubmitVo
                              ){

        // 提交订单
        Long orderId = orderService.submitOrder(tradeNo, orderSubmitVo);

        //TODO 删除购物车中选中的商品
        return Result.ok(orderId);
    }


    /**
     * 1、不写 BindingResult ，发生错误 boot会自动提示所有错误
     * 2、写了 BindingResult ，就一定得自己处理错误
     * @param vo
     * @param bindingResult
     * @return
     */
    @GetMapping("/haha")
    public Result test(@Valid ParamValidateTestVo vo, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            //如果有错
            List<FieldError> errors = bindingResult.getFieldErrors();
            List<ErrVo> errVos = errors.stream().map(e -> {

                return new ErrVo(e.getField(), e.getDefaultMessage());
            }).collect(Collectors.toList());


            return Result.build(errVos, ResultCodeEnum.PARAM_INVALIDA);
        }



        return Result.ok();
    }

}
