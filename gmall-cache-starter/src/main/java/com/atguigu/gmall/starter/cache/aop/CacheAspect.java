package com.atguigu.gmall.starter.cache.aop;



import com.atguigu.gmall.starter.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * aop就是filter思想；
 * 使用切面完成缓存的自动拦截逻辑
 *
 * 1、导入 aop-starter
 * 2、@EnableAspectJAutoProxy
 * 3、编写切面
 * 4、切入点表达式。
 * @Cache
 *      execution(* com.atguigu.gmall.**.SkuDetailServiceImpl.getSkuDetail(Long))
 *
 *      public OrderDetail getOrderDetail(Long orderId);
 *      public StockDetail getStockhaha(String skuName);
 *      public List<Order>  getUserOrder(String token,Long userId,String date);
 */

@Component //切面也必须放在springboot容器中才能起作用
@Aspect
public class CacheAspect {


    @Autowired
    CacheHelper cacheHelper;

    /**
     * 拦截方法：通知方法；
     *
     * 普通通知：只是通知，不能干扰目标方法执行
     * 前置通知：    前置通知 - 目标方法
     * 后置通知：    目标方法 - 目标方法以后 【后置通知】 finally
     * 返回通知：    目标方法 - 正常返回以后 【返回通知】 try
     * 异常通知：    目标方法 - 发生异常以后 【异常通知】 catch
     * 通知：目标方法到达指定阶段以后调用通知方法，通知方法只能感知，不能干扰
     *
     *  正常执行：  前置通知 --- 【目标方法】 --- 返回通知 --- 后置通知
     *  异常执行：  前置通知 --- 【目标方法】 --- 异常通知 --- 后置通知
     *
     *
     *
     * 环绕通知：编程式通知；干扰目标方法执行。决定是否执行目标方法
     * 1、方法必须返回Object。代理目标方法执行，返回数据
     * 2、参数必须是 ProceedingJoinPoint；
     *
     */
    @Around(value = "@annotation(com.atguigu.gmall.starter.cache.aop.annotation.Cache)")
    public Object around(ProceedingJoinPoint joinPoint){
        //获取目标方法参数
        Object[] args = joinPoint.getArgs();
        Object result = null;
        try {
            //动态计算表达式
            String cacheKey = cacheHelper.evaluteExpression(joinPoint);
            //1、先查缓存中有没有这个数据, 就是当前方法的返回值类型数据
            Object obj = cacheHelper.getCacheData(cacheKey,joinPoint);

            if(obj == null){
                //2、缓存中没有、准备回源
                //4、准备回源锁
                String lockKey = RedisConst.LOCK_PREFIX+cacheKey;


                //4.1、确定回源之前，先问布隆； 1）、用不用  2）、怎么用
                //判断布隆是否需要启用
                String bloomName = cacheHelper.determinBloom(joinPoint);
                if(StringUtils.isEmpty(bloomName)){
                    //TODO 不启用布隆。直接调用目标方法。并且要加锁
                    //4.1.1 布隆说有，尝试加锁
                    boolean tryLock = cacheHelper.tryLock(lockKey);
                    if(tryLock){
                        //5、加锁成功，回源。【调用目标方法做事】.放行目标方法进行查询
                        result = joinPoint.proceed(args); // 就是数据库查数据；执行目标方法。Object object = method.invoke(args);
                        //把数据存起来
                        cacheHelper.saveData(cacheKey,result);
                        //6、返回目标方法的数据
                        //7、解锁
                        cacheHelper.unlock(lockKey);
                        return result;
                    }

                    //7、没加锁成功，睡1s再查缓存
                    Thread.sleep(1000);
                    obj = cacheHelper.getCacheData(cacheKey, joinPoint);
                    //8、返回数据
                    return obj;
                }else {
                    //TODO 启用布隆，要自动用指定的布隆进行判断
                    boolean bloomcontains = cacheHelper.bloomTest(bloomName,joinPoint);
                    if(bloomcontains){
                        //4.1.1 布隆说有，尝试加锁
                        boolean tryLock = cacheHelper.tryLock(lockKey);
                        if(tryLock){
                            //5、加锁成功，回源。【调用目标方法做事】.放行目标方法进行查询
                            result = joinPoint.proceed(args); // 就是数据库查数据；执行目标方法。Object object = method.invoke(args);
                            //把数据存起来
                            cacheHelper.saveData(cacheKey,result);
                            //6、返回目标方法的数据
                            //7、解锁
                            cacheHelper.unlock(lockKey);
                            return result;
                        }

                        //7、没加锁成功，睡1s再查缓存
                        Thread.sleep(1000);
                        obj = cacheHelper.getCacheData(cacheKey, joinPoint);
                        //8、返回数据
                        return obj;
                    }else {
                        //4.1.2 布隆说没
                        return null;
                    }
                }

            }

            //3、缓存中有
            return obj;
            //返回通知
        } catch (Throwable e) {
            //异常通知

        } finally {
            //后置通知

        }
        //目标方法的结果进行返回，切面返回的是什么，调用目标方法的人（调用者）就会收到什么
        return result;
    }



}
