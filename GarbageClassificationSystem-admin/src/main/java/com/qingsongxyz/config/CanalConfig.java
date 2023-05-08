package com.qingsongxyz.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "canal")
@Data
public class CanalConfig {

    private String host;

    private Integer port;

    private String destination;

    private String database;

    private String username;

    private String password;

    @Bean
    public CanalConnector CanalConnector() {
        //1.创建连接
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(host, port),
                destination,
                username,
                password);

        //2.连接
        canalConnector.connect();

        //3.订阅数据库
        canalConnector.subscribe(database);

        return canalConnector;
    }
}
