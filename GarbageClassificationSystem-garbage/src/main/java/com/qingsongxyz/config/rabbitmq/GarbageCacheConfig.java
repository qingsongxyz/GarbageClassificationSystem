package com.qingsongxyz.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GarbageCacheConfig {

    public static final String GARBAGE_EXCHANGE_NAME = "garbage_exchange";

    public static final String GARBAGE_QUEUE_NAME = "garbage_queue";

    public static final String ROUTING_KEY = "garbage";

    @Bean
    public DirectExchange garbageExchange(){
        return ExchangeBuilder.directExchange(GARBAGE_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue garbageQueue(){
        return QueueBuilder.durable(GARBAGE_QUEUE_NAME).build();
    }

    @Bean
    public Binding exchangeToQueue(@Qualifier("garbageExchange") Exchange garbageExchange,
                                   @Qualifier("garbageQueue") Queue garbageQueue){
        return BindingBuilder.bind(garbageQueue).to(garbageExchange).with(ROUTING_KEY).noargs();
    }
}
