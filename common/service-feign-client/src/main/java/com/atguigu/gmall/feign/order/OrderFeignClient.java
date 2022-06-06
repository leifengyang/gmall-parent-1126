package com.atguigu.gmall.feign.order;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/rpc/inner/order")
@FeignClient("service-order")
public interface OrderFeignClient {


    /**
     * 1、feign帮我们发送请求，要到数据
     * 2、feign帮我们转成指定的返回值类型
     *
     * 我们此时完全可以告诉Feign，不用转成精确类型，只需要转成map（json-vo）；
     *
     * @return
     */
    @GetMapping("/confirm")
    Result<Map<String,Object>> getOrderConfirmData();
}
