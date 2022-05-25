package com.atguigu.gmall.item;


import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class BloomTest {

    @Autowired
    RBloomFilter<Object> skuIdBloom;

    //spring会自动从容器中拿到所有 RBloomFilter<Object> 类型的组件 ，放到map中，map的key就是组件的名
    @Autowired
    Map<String,RBloomFilter<Object>> bloomMap;


    @Test
    public void hahaha(){
        System.out.println(bloomMap);
    }


    @Test
    public void testHaha(){
        System.out.println(skuIdBloom.contains("47")); //false
        System.out.println(skuIdBloom.contains(47)); //false
        System.out.println(skuIdBloom.contains(47L)); //true
        System.out.println(skuIdBloom.contains(new Integer(47))); //false
        System.out.println(skuIdBloom.contains(new Long(47))); //true


        //给布隆存一个东西  new Person();

//        SkuInfo skuInfo = new SkuInfo();
//        Long id = skuInfo.getId();
//        skuIdBloom.add(id);
    }
}
