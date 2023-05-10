package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.example.feign.UserFeignService;
import org.example.loadbalanced.CustomlLoadBalancedConffiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.example.entity.CommonResult;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("cart")
@LoadBalancerClient(name="provider-server",configuration = CustomlLoadBalancedConffiguration.class )
public class CartController {
    @Autowired
    private UserFeignService userFeignService;

    @GetMapping("/hello")
    @CircuitBreaker(name = "backendA",fallbackMethod = "fallbackHello")
    public String hello(){
        return userFeignService.Hello();
    }

    @GetMapping("/addCart/{userId}")
//    @CircuitBreaker(name = "backendA",fallbackMethod = "fallback")
//    @Bulkhead(name = "bulkheadB", fallbackMethod = "fallback", type = Bulkhead.Type.SEMAPHORE)
//    @Bulkhead(name = "bulkheadA", fallbackMethod = "fallback", type = Bulkhead.Type.THREADPOOL)
    @RateLimiter(name = "ratelimiterA", fallbackMethod = "fallback")
    public CommonResult<User> addCart(@PathVariable Integer userId) throws InterruptedException{
        System.out.println("jinru");
//        Thread.sleep(10000L);//阻塞10秒
        CommonResult<User> list = userFeignService.getUserById(userId);
        System.out.println("likai");
        return list;
    }
//    public CompletableFuture<User> addCart(@PathVariable Integer userId) throws InterruptedException{
//        System.out.println("jinru");
////        Thread.sleep(10000L);//阻塞10秒
//        CompletableFuture<User> result = CompletableFuture.supplyAsync(()->{
//            return userFeignService.getUserById(userId).getResult();
//        });
//        System.out.println("likai");
//        return result;
//    }
    public CommonResult<User> fallback(Integer userId,Throwable e){
        e.printStackTrace();
        System.out.println("fallback已经调用");
        CommonResult<User> result = new CommonResult<>(400,"fallback",new User());
        return result;
    }

//    public CompletableFuture<User> fallback(Integer userId,Throwable e){
//        e.printStackTrace();
//        System.out.println("fallback已经调用");
//        CompletableFuture<User> result = CompletableFuture.supplyAsync(()->{
//            return new CommonResult<>(400,"fallback",new User()).getResult();
//        });
//        return result;
//    }

    public String fallbackHello(Throwable e){
        e.printStackTrace();
        System.out.println("fallback已经调用");
        String s = "fallback";
        return s;
    }


    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey", blockHandler = "dealHandler_testHotKey")
    public String testHotKey(@RequestParam(value = "p1", required = false) String p1,
                             @RequestParam(value = "p2", required = false) String p2) {
        System.out.println("------testHotKey");
        return "------testHotKey";
    }

    public String dealHandler_testHotKey(String p1, String p2, BlockException exception) {
        // 默认 Blocked by Sentinel (flow limiting)
        System.out.println("-----dealHandler_testHotKey");
        return "-----dealHandler_testHotKey";
    }
}
