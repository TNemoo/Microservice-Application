package com.javaguru.contactservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ContactServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContactServiceApplication.class, args);
    }
}

/** Так можно запускать при старте микросервиса из main: */
//public class ContactServiceApplication implements CommandLineRunner {
//
//    @Value("${config-name}")
//    private String name;
//
//    public static void main(String[] args) {
//        SpringApplication.run(ContactServiceApplication.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("Config name: " + name);
//    }
//}