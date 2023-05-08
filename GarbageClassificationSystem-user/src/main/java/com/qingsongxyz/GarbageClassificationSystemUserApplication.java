package com.qingsongxyz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.qingsongxyz"}, exclude = {DataSourceAutoConfiguration.class})
public class GarbageClassificationSystemUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarbageClassificationSystemUserApplication.class, args);
    }

}
