package com.atguigu.gmall.exception;


import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 当前应用的全局异常处理器
 * 1、@ControllerAdvice: 这个组件是所有 @Controller 组件的切面
 * 2、@ResponseBody：返回的对象以json方式写出
 */


//@ControllerAdvice
//@ResponseBody

@Slf4j
@RestControllerAdvice //全局异常处理切面
public class AppGlobalExceptionHandler {

    @Value("${spring.application.name}")
    String applicationName;

    /**
     * 业务异常处理器
     *
     * 浏览器----Controller---Service---出现异常----全局异常处理
     *
     * RequestContextHolder：利用ThreadLocal把当前请求放到当前线程中
     *
     * @param e
     * @return
     */
    @ExceptionHandler(GmallException.class)
    public Result handleGmallException(GmallException e){
        log.error("全局异常捕获：业务异常：{}",e);
        Result fail = Result.fail();
        fail.setCode(e.getCode());
        fail.setMessage(e.getMessage());

        //异常的常用信息
        //1、请求
        //2、参数
        //获取当前请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //1、得到请求路径
        StringBuffer url = request.getRequestURL();
        //2、得到请求的所有参数
        Map<String, String[]> map = request.getParameterMap();

        Map<String,Object> reqInfo = new HashMap<>();
        reqInfo.put("path",url);
        reqInfo.put("params",map);
        reqInfo.put("serviceName",applicationName);

        fail.setData(reqInfo);
        return fail;
    }

    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e){
        log.error("全局异常捕获：系统异常：{}",e);
        Result fail = Result.fail();
        fail.setCode(500);
        fail.setMessage("服务器内部异常");
        return fail;
    }

}
