package com.atguigu.gmall.web.config;

import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloFilter  extends HttpFilter {


    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        //放行
//        filterChain.doFilter(servletRequest,servletResponse);

        //转发
//        servletRequest.getRequestDispatcher("/login.html").forward(servletRequest,servletResponse);

        //重定向
        response.sendRedirect("http://baidu.com");

    }
}
