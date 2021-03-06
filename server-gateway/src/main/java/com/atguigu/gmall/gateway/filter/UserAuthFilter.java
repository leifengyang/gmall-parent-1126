package com.atguigu.gmall.gateway.filter;


import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.gateway.properties.AuthProperties;
import com.atguigu.gmall.model.user.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * 处理用户认证功能
 * 1、根据用户token把用户userid透传
 * 2、基本的鉴权
 * <p>
 * 请求  ==== 过滤器 ==== 放行 ==== 转给其他微服务 ====  响应 === 交给浏览器
 * <p>
 * 响应式的想法就是用更少量的线程； 模拟高并发和吞吐量；
 * go 协程；轻量级线程思想
 * mainThread1(); === 1w管道 ===  Map<String,Object> 一个线程能维护无数管道
 * mainThread2(); === 1w管道 ===  Map<String,Object>  无敌快。
 * <p>
 * new Thread();new Thread();new Thread();new Thread();new Thread();
 * <p>
 * redis快？单线程； 快，不需要线程切换开销；
 */
@Slf4j
@Component
public class UserAuthFilter implements GlobalFilter {


    @Autowired
    AuthProperties authProperties;

    //ant风格路径匹配器
    AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    StringRedisTemplate redisTemplate;

//    public static void main(String[] args) {
//        //1、数据发布者
////        Mono<Integer> mono = Mono.just(1);
////
////
////        //2、数据订阅
////        mono.subscribe((data)->{
////            System.out.println("A:"+data);
////        });
////
////        mono.subscribe((data)->{
////            System.out.println("B:"+data);
////        });
////
////
////        mono.subscribe((data)->{
////            System.out.println("C:"+data);
////        });
//
//
//        //2、发很多数据；产生源源不断的数据
//        Flux<Long> flux = Flux.interval(Duration.ofMillis(3000));
//
//        System.out.println("开始");
//        flux.subscribe((data)->{
//            System.out.println("A:"+data);
//        });
//
//        flux.subscribe((data)->{
//            System.out.println("B:"+data);
//            //干更多事情
//        });
//
//        flux.subscribe((data)->{
//            System.out.println("C:"+data);
//        });
//
//        System.out.println("哈哈哈哈");
//        try {
//            Thread.sleep(1000000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 所有请求到达目标服务之前，都得先过这个方法
     *
     * 分析所有请求带来的 token或者 userTempId把他们透传给其他微服务
     *
     * @param exchange 代表原来的请求和响应
     * @param chain    代表原来的过滤器链
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //非响应式：HttpServletRequest、HttpServletResponse；Servlet-API
        //响应式： ServerHttpRequest、ServerHttpResponse
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath(); //只要访问路径
        log.info("UserAuthFilter 开始拦截；请求路径：{}", path);


        //继续往下放 Mono<Void>.subcribe( ) Mono<Void> .subcribe();

        //1、进行认证、鉴权

        //【1】、所有人都能访问的路径直接放行； /js/**，/**/css/**,/img/**， /abc/hello/aaa.avi
        // ant风格的路径
        // *代表多个字符，             ?代表单个字符  **任意多层路径与多个字符
        // /js/**，                  js下的任意请求
        // /a?c/*/aa?*.?ng          /abc/aaa/aax.jng 可以匹配
        // /aa/*/b/**/?a.h?n*p      /aa/xaa/b/ba.honoooop 可以匹配

        List<String> anyoneurls = authProperties.getAnyoneurls();
        for (String anyoneurl : anyoneurls) {
            //path // /img/icons.png
            boolean match = matcher.match(anyoneurl, path);
            if (match) {
                //说明当前path是anyoneurls声明的任何人都能访问的路径
                return chain.filter(exchange);
            }
        }


        //【2】、任何情况下都不能敲浏览器来网关进行往下访问的
        List<String> denyurls = authProperties.getDenyurls();
        for (String denyurl : denyurls) {
            boolean match = matcher.match(denyurl, path);
            if (match) {
                //1、构造响应
                Result result = Result.build("", ResultCodeEnum.forbidden);
                //2、转成json
                String str = JSONs.toStr(result);

                //3、得到 DataBuffer
                DataBuffer wrap = exchange.getResponse()
                        .bufferFactory()
                        .wrap(str.getBytes(StandardCharsets.UTF_8));

                //4、将 DataBuffer 发布出去
                Publisher<? extends DataBuffer> body = Mono.just(wrap);

                //5、防止乱码，在响应头中告诉浏览器纯文本数据的编码格式
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);

                //6、Response 其实是一个响应数据的订阅者
                return exchange.getResponse()
                        .writeWith(body);
            }
        }


        //【3】、必须登录才能访问的
        for (String authurl : authProperties.getAuthurls()) {
            boolean match = matcher.match(authurl, path);
            if (match) {
                //检查是否已经登录
                //如果没有登录直接打回
                boolean check = validateToken(request);
                if (!check) {
                    //返回fasle，代表：1）、没带令牌  2）、带了令牌但是是假的
                    //打回？调到登录页
                    return locationToLoginPage(exchange);
                }
            }
        }


        //【4】正常请求；如果登录需要透传UserId
        //1、前端没带还是带错了
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            //没带没事，直接放行； 有可能带了 userTempId；把这个也透传
            String userTempId = getUserTempId(request);
            //透传
            ServerHttpRequest newRequest = exchange.getRequest()
                    .mutate() //克隆一个
                    .header("UserTempId", userTempId)
                    .build();

            //从原exchange克隆一个新的exchange
            ServerWebExchange build = exchange.mutate() //克隆一个exchange
                    .request(newRequest)
                    .response(exchange.getResponse())
                    .build();

            return chain.filter(build);
        } else {
            //带了就必须对，
            boolean validate = validateToken(request);
            if (!validate) {
                //带错了
                return locationToLoginPage(exchange);
            } else {
                //带对了, userId往下透传  exchange（请求，响应）
                ServerHttpRequest orginRequest = exchange.getRequest();

                //拿到userInfo信息
                UserInfo userInfo = getTokenRedisValue(token, IpUtil.getGatwayIpAddress(orginRequest));
                //获取临时id
                String userTempId = getUserTempId(request);
                //自己加一个UserId请求头； 原始请求的请求头不允许修改
//                orginRequest.getHeaders().add("UserId",userInfo.getId().toString());
                //从原请求克隆一个新请求
                ServerHttpRequest newRequest = exchange.getRequest()
                        .mutate() //克隆一个
                        .header("UserId", userInfo.getId().toString())
                        .header("UserTempId",userTempId)
                        .build();

                //从原exchange克隆一个新的exchange
                ServerWebExchange build = exchange.mutate() //克隆一个exchange
                        .request(newRequest)
                        .response(exchange.getResponse())
                        .build();


                return chain.filter(build);
            }
        }


    }

    /**
     * 获取用户临时id  userTempId
     *
     * @param request
     * @return
     */
    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";

        HttpCookie cookie = request.getCookies().getFirst("userTempId");
        //有这个cookie，说明前端把token放到的cookie位置，给我们带来了
        if (cookie != null) {
            userTempId = cookie.getValue();
        } else {
            //前端没有放在cookie位置
            String headerValue = request.getHeaders().getFirst("userTempId");
            userTempId = headerValue;
        }

        return userTempId;
    }

    /**
     * 重定向到登录页
     *
     * @param exchange
     * @return
     */
    private Mono<Void> locationToLoginPage(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //1、httpcode: 302
        response.setStatusCode(HttpStatus.FOUND);

        //2、响应头 Location: 新位置
        String originUrl = request.getURI().toString();
        URI uri = URI.create(authProperties.getLoginPage() + "?originUrl=" + originUrl);
        response.getHeaders().setLocation(uri);
        //API更规范了； 更原理；
        //3、响应结束

        //4、命令浏览器删除之前的假令牌（删cookie，服务器只需要给浏览器发一个同名cookie）
        //maxAge: 正数[指定时间后过期] 0[立即过期] 负数[浏览器关闭以后才过期(会话cookie)]
        ResponseCookie cookie = ResponseCookie
                .from("token", "8383279")
                .maxAge(0L)
                .domain(".gmall.com")
                .build();
        //放一个同名cookie，命令浏览器立即删除
        response.addCookie(cookie); //解决循环重定向问题

        return response.setComplete();
    }


    private String getToken(ServerHttpRequest request) {
        String token = "";

        HttpCookie cookie = request.getCookies().getFirst("token");
        //有这个cookie，说明前端把token放到的cookie位置，给我们带来了
        if (cookie != null) {
            token = cookie.getValue();
        } else {
            //前端没有放在cookie位置
            String headerToken = request.getHeaders().getFirst("token");
            token = headerToken;
        }

        return token;
    }

    /**
     * 验证令牌
     * 1、获取前端带来的token
     * 2、如果有token就去redis查一下
     *
     * @param request
     * @return
     */
    private boolean validateToken(ServerHttpRequest request) {

        //1、获取到token；【Cookie:token=xxxx】【直接在头中有个token=xxx】
        String token = "";

        HttpCookie cookie = request.getCookies().getFirst("token");
        //有这个cookie，说明前端把token放到的cookie位置，给我们带来了
        if (cookie != null) {
            token = cookie.getValue();
        } else {
            //前端没有放在cookie位置
            String headerToken = request.getHeaders().getFirst("token");
            token = headerToken;
        }

        if (StringUtils.isEmpty(token)) {
            //前端没有带token
            return false;
        } else {
            //前端带了 token；校验一下
            //  user:login:token 查询下redis中真正的值
            String ipAddress = IpUtil.getGatwayIpAddress(request);
            UserInfo loginUser = getTokenRedisValue(token, ipAddress);
            if (loginUser == null) {
                //用户没登录或者假登录
                return false;
            }
            return true;
        }
    }


    /**
     * 查询redis中这个token对应的值
     *
     * @param token
     * @param ipAddress
     * @return
     */
    public UserInfo getTokenRedisValue(String token, String ipAddress) {
        //1、去redis查询token对应的真正值
        String json = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_PREFIX + token);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            //redis有数据
            UserInfo info = JSONs.strToObj(json, new TypeReference<UserInfo>() {
            });

            //
            if (!info.getIpAddr().equals(ipAddress)) {
                //redis中当时登录的用户的ip和现在正在请求的这个用户的ip不一致，有可能发生了盗用，或者网络环境发生了变化都需简要重新登录
                return null;
            }

            return info;
        }
    }
}
