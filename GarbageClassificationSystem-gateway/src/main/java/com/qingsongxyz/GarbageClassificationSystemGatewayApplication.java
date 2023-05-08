package com.qingsongxyz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GarbageClassificationSystemGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarbageClassificationSystemGatewayApplication.class, args);
    }

}
