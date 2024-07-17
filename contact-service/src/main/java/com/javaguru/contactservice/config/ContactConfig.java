package com.javaguru.contactservice.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ContactConfig {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    @LoadBalanced
    /** аннотация отправляет запросы RestTemplate через eureka-server */
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean //настройка встроенного обработчика ошибок библиотеки Feign Client
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean //настройка встроенного обработчика ошибок библиотеки Feign Client
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
