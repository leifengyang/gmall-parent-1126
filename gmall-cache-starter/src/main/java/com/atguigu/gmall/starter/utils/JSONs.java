package com.atguigu.gmall.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class JSONs {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String toStr(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转换JSON的String异常:{}",obj);
        }
        return null;
    }


    public static <T> T strToObj(String json, TypeReference<T> typeReference) {

        T t = null;
        try {
            t = objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;

    }

    public static <T extends Object> T nullInstance(TypeReference<T> typeReference) {
        String json = "[]";  //Person
        T t = null;

        try {
            t = objectMapper.readValue(json, typeReference);
            //泛型套泛型  List<Map<String,Hello>>
            //aaa
        } catch (JsonProcessingException e) {
            log.error("准备空示例异常：{}",e);
            try {
                t = objectMapper.readValue("{}",typeReference);
            } catch (JsonProcessingException ex) {
                log.error("你这不是json");
            }
        }
        return t;
    }
}
