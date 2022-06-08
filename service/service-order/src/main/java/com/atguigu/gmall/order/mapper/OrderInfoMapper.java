package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author lfy
* @description 针对表【order_info_1(订单表 订单表)】的数据库操作Mapper
* @createDate 2022-06-02 16:31:09
* @Entity com.atguigu.gmall.order.domain.OrderInfo1
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    void updateOrderStatus(@Param("originStatus") String originStatus,
                           @Param("orderInfo") OrderInfo orderInfo);

    void updateOrderStatusToPaid(@Param("outTradeNo") String outTradeNo,
                                 @Param("userId") Long userId,
                                 @Param("processStatus") String processStatus,
                                 @Param("orderStatus") String orderStatus);
}




