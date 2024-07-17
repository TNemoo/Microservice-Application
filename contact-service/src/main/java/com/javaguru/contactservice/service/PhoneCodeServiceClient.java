package com.javaguru.contactservice.service;

import feign.Request;
import feign.Retryer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PHONE-CODE-SERVICE")
public interface PhoneCodeServiceClient {

    //аннотация CircuitBreaker, name = "phone-code-service" соответствует имени в yml (instances: phone-code-service:)
    @CircuitBreaker(name = "phone-code-service", fallbackMethod = "getIdByCodeFallBack")
    @GetMapping("/codes")
    Integer getIdByCode(@RequestParam("code") Integer code);

    // метод, который CircuitBreaker будет выполнять в случае отказа МС PHONE-CODE-SERVICE:
    default Integer getIdByCodeFallBack(Integer code, Throwable throwable) {
        System.out.println("   code: " + code + ", exception: " + throwable.getMessage());
        return Integer.MAX_VALUE;
    }


    //аннотация CircuitBreaker, name = "phone-code-service" соответствует имени в yml (instances: phone-code-service:)
    @CircuitBreaker(name = "phone-code-service", fallbackMethod = "getCodeByIdFallBack")
    @GetMapping("/codes/{id}")
    Integer getCodeById(@PathVariable("id") Integer id);

    // метод, который CircuitBreaker будет выполнять в случае отказа МС PHONE-CODE-SERVICE:
    default Integer getCodeByIdFallBack(Integer id, Throwable throwable) {
        System.out.println("   code: " + id + ", exception: " + throwable.getMessage());
        return Integer.MAX_VALUE;
    }
}
//
//class Configuration {
//    @Bean
//    public Retryer retryer() {
//        return new Retryer.Default();
//    }
//
//    @Bean
//    public Request.Options options() {
//        return new Request.Options(5000, 10000);
//    }
//}