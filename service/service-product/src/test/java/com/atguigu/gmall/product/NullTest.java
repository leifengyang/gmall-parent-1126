package com.atguigu.gmall.product;


import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.config.threadpool.AppThreadPoolProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class NullTest {

    @Test
    public void nullTest(){
        //集合
        List<Map<String, String>> list = JSONs.nullInstance(new TypeReference<List<Map<String, String>>>() {
        });
        System.out.println(list);


        //对象
        AppThreadPoolProperties instance = JSONs.nullInstance(new TypeReference<AppThreadPoolProperties>() {
        });
        System.out.println(instance);
    }
}
