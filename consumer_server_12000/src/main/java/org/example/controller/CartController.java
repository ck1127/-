package org.example.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
