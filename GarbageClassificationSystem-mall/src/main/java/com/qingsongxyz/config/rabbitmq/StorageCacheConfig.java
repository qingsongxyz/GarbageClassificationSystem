package com.qingsongxyz.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageCacheConfig {

    public static final String STORAGE_EXCHANGE_NAME = "storage_exchange";

    public static final String STORAGE_QUEUE_NAME = "storage_queue";

    public static final String STORAGE_ROUTING_KEY = "storage";

    @Bean
    public DirectExchange storageExchange(){
        return ExchangeBuilder.directExchange(STORAGE_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue storageQueue(){
        return QueueBuilder.durable(STORAGE_QUEUE_NAME).build();
    }

    @Bean
    public Binding exchangeToQueue(@Qualifier("storageExchange") Exchange storageExchange,
                                   @Qualifier("storageQueue") Queue storageQueue){
        return BindingBuilder.bind(storageQueue).to(storageExchange).with(STORAGE_ROUTING_KEY).noargs();
    }
}
