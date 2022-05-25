package com.atguigu.gmall.item;

import org.junit.Test;

import java.lang.reflect.Method;

public class ReflectTest {

    @Test
    public void getMethodReturnType(){
//        Hello hello = new Hello();

        for (Method method : Hello.class.getMethods()) {
            System.out.println("方法名："+method.getName() + "； 返回值类型："+method.getGenericReturnType());
        }

    }
}
