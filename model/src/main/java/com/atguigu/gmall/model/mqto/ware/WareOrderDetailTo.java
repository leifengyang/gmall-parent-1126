package com.atguigu.gmall.model.mqto.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class WareOrderDetailTo {
    private Long skuId;
    private Integer skuNum;
    private String skuName;
}
