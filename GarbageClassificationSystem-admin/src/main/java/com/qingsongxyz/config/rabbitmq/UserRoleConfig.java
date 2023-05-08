package com.qingsongxyz.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRoleConfig {

    //用户角色交换机、队列、路由键
    public static final String USER_ROLE_EXCHANGE_NAME = "user_role_exchange";

    public static final String USER_ROLE_QUEUE_NAME = "user_role_queue";

    public static final String USER_ROLE_ROUTING_KEY= "user_role";

    @Bean
    public DirectExchange userRoleExchange(){
        return ExchangeBuilder.directExchange(USER_ROLE_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue userRoleQueue(){
        return QueueBuilder.durable(USER_ROLE_QUEUE_NAME).build();
    }

    @Bean
    public Binding exchangeToQueue(@Qualifier("userRoleExchange") Exchange userRoleExchange,
                                   @Qualifier("userRoleQueue") Queue userRoleQueue){
       return BindingBuilder.bind(userRoleQueue).to(userRoleExchange).with(USER_ROLE_ROUTING_KEY).noargs();
    }
}
