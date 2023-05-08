package com.qingsongxyz.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketQueryConfig {

    //三方登录相关交换机、路由键
    public static final String THIRD_PARTY_EXCHANGE_NAME = "third_party_exchange";

    public static final String THIRD_PARTY_ROUTING_KEY_PREFIX = "thirdParty";

    //聊天相关交换机、路由键
    public static final String DEFAULT_TOPIC_EXCHANGE_NAME = "amq.topic";

    public static final String CHAT_ROUTING_KEY_PREFIX = "chat";

    @Bean("third_party_exchange")
    public TopicExchange websocket_exchange(){
        return ExchangeBuilder.topicExchange(THIRD_PARTY_EXCHANGE_NAME).durable(true).build();
    }

}
