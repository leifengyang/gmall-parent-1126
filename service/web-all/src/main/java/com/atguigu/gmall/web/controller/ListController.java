package com.atguigu.gmall.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 检索功能控制器
 */
@Controller
public class ListController {

    //list.html?category3Id=61
    @GetMapping("/list.html")
    public String searchPage(){

        //TODO 远程调用检索服务去检索

        //来到检索页
        return "list/index";
    }
}
