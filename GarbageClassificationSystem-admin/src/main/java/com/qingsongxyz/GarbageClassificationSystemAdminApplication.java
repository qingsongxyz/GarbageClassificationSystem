package com.qingsongxyz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients(basePackages = "com.qingsongxyz.service.feignService")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.qingsongxyz"})
public class GarbageClassificationSystemAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarbageClassificationSystemAdminApplication.class, args);
    }
}
