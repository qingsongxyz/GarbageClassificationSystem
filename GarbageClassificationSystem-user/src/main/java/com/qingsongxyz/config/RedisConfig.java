package com.qingsongxyz.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    //向容器中注入RedissonClient
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        //添加redis单节点配置
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);

        //创建客户端
        return Redisson.create(config);
    }
}
