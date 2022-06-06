package com.atguigu.gmall.feign.ware;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//url：绝对路径，以后的请求不会找注册中心要 ware-manage 的地址
//保留value的意义就可以精确配置这个feign的行为
//@FeignClient(value = "ware-manage",url="http://www.baidu.com")
@FeignClient(value = "ware-manage", url = "${app.props.ware-url}")
public interface WareFeignClient {

    // https://www.baidu.com/s?wd=哈哈
//    @GetMapping("/s")
//    String search(@RequestParam("wd") String key);


    /**
     * 检查一个商品的库存
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num);




}
