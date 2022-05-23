package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;


/**
 * 1、new Thread
 * <p>
 * 2、异步(CompletableFuture)+线程池(ThreadPoolExecutor)
 * CompletableFuture：
 *
 *
 * 使用线程池
 * 1、准备自定义一个线程池
 * 2、CompletableFuture 给线程池中提交任务
 * 3、对提交的任务进行编排、组合、容错处理
 */
@SpringBootTest  //这是一个SpringBoot测试
public class AppThreadPoolTest {

    //    @Qualifier("corePool")
    @Autowired
    ThreadPoolExecutor poolExecutor;




    @Test
    public void zuheTest() throws Exception {
        CompletableFuture<Void> async1 = CompletableFuture.runAsync(() -> {
            System.out.println("打印A");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("A");
        }, poolExecutor);



        CompletableFuture<Void> async2 =CompletableFuture.runAsync(()->{
            System.out.println("查询B");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("B");
        },poolExecutor);


        CompletableFuture<Void> async3 =CompletableFuture.runAsync(()->{
            System.out.println("保存C");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("C");
        },poolExecutor);




        //多任务组合
//        CompletableFuture.allOf(async1,async2,async3)
//                .whenComplete((a,b)->{
//                    System.out.println("结果:"+a);
//                    System.out.println("异常："+b);
//                    System.out.println("D");
//                });

        //只是一个等不到拉倒
        try {
            CompletableFuture.anyOf(async1,async2,async3).get(100, TimeUnit.MILLISECONDS);
            System.out.println("正常逻辑");
        }catch (TimeoutException e){
            System.out.println("超时逻辑");
        }




//        //1、这三个任务全部完成以后 打印D
//        long start = System.currentTimeMillis();
//        System.out.println("start....");
//        //其实等了最长时间
//        async2.get(); //1s
//        async3.get(); //2s
//        async1.get(); //3s
//
//        long end = System.currentTimeMillis();
//        System.out.println("D："+(end-start)/1000);

    }


    /**
     * then系列进行任务编排
     * 1、thenRun：  传入 Runnable 启动一个无返回值的异步任务，
     *      thenRun
     *      thenRunAsync
     *      thenRunAsync(带线程池)
     * 2、thenAccept:   传入 Consumer  void accept(T t); 接参数，但是也无返回值
     *      thenAccept
     *      thenAcceptAsync
     *      thenAcceptAsync(带线程池)
     * 3、thenApply: 传入  Function:  R apply(T t);  而且有返回值
     *
     */
    @Test
    public void thenTest() throws ExecutionException, InterruptedException {
        //1、计算
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：正在计算");
            int i = 1 + 1;
            return i;
        }, poolExecutor).thenApplyAsync((result) -> {
            System.out.println(Thread.currentThread().getName() + "：正在转换");
            return result + 10;
        }, poolExecutor).thenApplyAsync((result) -> {
            System.out.println(Thread.currentThread().getName() + "：变成字母");
            return result + "A";
        });





        String s = future.get();
        System.out.println("结果："+s);

        //2、数据转换  +10  12



        //3、变成字母  12A




    }


    /**
     * CompletableFuture future
     * 1、thenXXX： 前一个任务结束以后，继续做接下来的事情
     * 2、whenXxx: when的事件回调
     * whenComplete： 完成后干啥
     * 前一个任务.whenComplete((t,u)->{ 处理t[上一步结果],u[上一步异常] })
     * xxxxAsync： 带了Async代表这些方法运行需要开新线程
     * 指定线程池：  就在指定线程池中开新线程
     * 3、exceptionally： 前面异常以后干什么
     */

    @Test
    public void lianshidiaoyong() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName() + "：正在计算");
                    Double random = Math.random() * 100;
                    return random.intValue();
                }, poolExecutor)
                .exceptionally((t) -> {
                    System.out.println("zhale:" + t);
                    return 222;
                });

        System.out.println("结果：" + future.get());
    }


    @Test
    public void exceptionTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> aaa = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：正在计算");
            Double random = Math.random() * 100;
            return random.intValue() / 0;
        }, poolExecutor);

        //异常才会运行 RPC 熔断
        CompletableFuture<Integer> exceptionally = aaa.exceptionally((exception) -> {
            System.out.println("上一步炸了：" + exception);
            return 1;
        });


        Integer integer = exceptionally.get();
        System.out.println("结果：" + integer);


    }

    @Test
    public void bianpaiTest() {

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("哈哈");
            int i = 10 / 0;
        }, poolExecutor);

        // void accept(T t, U u);
        future.whenComplete((t, u) -> {
            System.out.println("t:" + t);
            System.out.println("u:" + u);
        });

        // R apply(T t); 异常回调
        future.exceptionally((t) -> {
            System.out.println("上次的异常：" + t);
            return null;
        });


//        Void unused = future.get();


        CompletableFuture<Integer> aaa = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：正在计算");
            Double random = Math.random() * 100;
            return random.intValue();
        }, poolExecutor);

        aaa.whenCompleteAsync((a, b) -> {
            System.out.println(Thread.currentThread().getName() + "： when");
            if (b != null) {
                //异常不为null
                System.out.println("b:" + b);
            } else {
                System.out.println("a:" + a);
            }

        }, poolExecutor);


        //get就是阻塞等待
//        future.get();
//        System.out.println("///");


        //xxxxxxxx

    }


    /**
     * 启动一个任务: 返回一个 CompletableFuture
     */
    @Test
    public void startAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "哈哈啊...");
        }, poolExecutor);

        /**
         * @FunctionalInterface
         * public interface Supplier<T> {
         *
         *
         *      * Gets a result.
         *      *
         *      * @return a result
         *
         *  T get ();
         *  }
         */
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：正在计算");
            Double random = Math.random() * 100;
            return random.intValue();
        }, poolExecutor);


        Integer result = future.get(); //阻塞等待
        System.out.println("结果:" + result);
    }


    /**
     * 验证线程池
     */
//    @Transactional //所有测试期间的数据会被自动回滚
    @Test
    public void poolExecutorTest() {

        System.out.println("线程池：" + poolExecutor);

        int corePoolSize = poolExecutor.getCorePoolSize();
        System.out.println(poolExecutor.getQueue().remainingCapacity());


    }

}
