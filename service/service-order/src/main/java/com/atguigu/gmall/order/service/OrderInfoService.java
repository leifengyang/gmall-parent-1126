package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author lfy
* @description 针对表【order_info_1(订单表 订单表)】的数据库操作Service
* @createDate 2022-06-02 16:31:09
*/
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 原子修改订单状态。
     * @param originStatus  期望订单原始状态是什么
     * @param modifyStatus  最终将订单修改成什么
     * @param orderId  订单id
     * @param userId   用户id
     */
    void updateOrderStatus(ProcessStatus originStatus, ProcessStatus modifyStatus, Long orderId, Long userId);

    /**
     * 修改订单为已支付
     * @param outTradeNo
     * @param userId
     * @param processStatus
     * @param orderStatus
     */
    void updateOrderStatusToPaid(String outTradeNo, long userId, String processStatus, String orderStatus);

    /**
     * 根据tradeNo找到订单以及订单详情；
     * @param outTradeNo
     * @return
     */
    List<OrderInfo> getOrderAndDetailByOutTradeNo(String outTradeNo);

    /**
     * 修改订单状态为已拆分
     * @param split
     * @param userId
     * @param id
     */
    void updateOrderStatusToSpilt(ProcessStatus split, Long userId, Long id);

    /**
     * 修改订单状态
     * @param orderId
     * @param newStatus
     */
    void updateStatusByOrderId(Long orderId, ProcessStatus newStatus);
}
