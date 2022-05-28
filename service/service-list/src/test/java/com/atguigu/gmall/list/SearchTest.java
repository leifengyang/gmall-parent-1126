package com.atguigu.gmall.list;

import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.SearchParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchTest {


    @Autowired
    GoodsSearchService searchService;
    @Test
    public void searchTest(){
        SearchParam param = new SearchParam();
        param.setCategory3Id(61L);

        searchService.search(param);
    }
}
