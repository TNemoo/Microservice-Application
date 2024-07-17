package com.javaguru.phonecodeservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class PhoneCodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhoneCodeServiceApplication.class, args);
	}
	@Bean
	public ModelMapper mapper() {
		return new ModelMapper();
	}
}
