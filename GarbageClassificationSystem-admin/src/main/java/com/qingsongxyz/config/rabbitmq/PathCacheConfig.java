package com.qingsongxyz.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathCacheConfig {

    public static final String ROLE_PATH_EXCHANGE_NAME = "role_path_exchange";

    public static final String ROLE_PATH_QUEUE_NAME = "role_path_queue";

    public static final String ROLE_PATH_ROUTING_KEY = "role_path";

    public static final String ROLE_QUEUE_NAME = "role_queue";

    public static final String ROLE_ROUTING_KEY = "role";

    public static final String PATH_QUEUE_NAME = "path_queue";

    public static final String PATH_ROUTING_KEY = "path";

    @Bean
    public DirectExchange rolePathExchange(){
        return ExchangeBuilder.directExchange(ROLE_PATH_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue rolePathQueue(){
        return QueueBuilder.durable(ROLE_PATH_QUEUE_NAME).build();
    }

    @Bean
    public Queue roleQueue(){
        return QueueBuilder.durable(ROLE_QUEUE_NAME).build();
    }
    @Bean
    public Queue pathQueue(){
        return QueueBuilder.durable(PATH_QUEUE_NAME).build();
    }

    @Bean
    public Binding exchangeToRolePathQueue(@Qualifier("rolePathExchange") Exchange rolePathExchange,
                                   @Qualifier("rolePathQueue") Queue rolePathQueue){
        return BindingBuilder.bind(rolePathQueue).to(rolePathExchange).with(ROLE_PATH_ROUTING_KEY).noargs();
    }

    @Bean
    public Binding exchangeToRoleQueue(@Qualifier("rolePathExchange") Exchange rolePathExchange,
                                   @Qualifier("roleQueue") Queue roleQueue){
        return BindingBuilder.bind(roleQueue).to(rolePathExchange).with(ROLE_ROUTING_KEY).noargs();
    }

    @Bean
    public Binding exchangeToPathQueue(@Qualifier("rolePathExchange") Exchange rolePathExchange,
                                   @Qualifier("pathQueue") Queue pathQueue){
        return BindingBuilder.bind(pathQueue).to(rolePathExchange).with(PATH_ROUTING_KEY).noargs();
    }
}
