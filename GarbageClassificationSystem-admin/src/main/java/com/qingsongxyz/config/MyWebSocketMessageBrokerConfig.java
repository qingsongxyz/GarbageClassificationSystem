package com.qingsongxyz.config;

import com.qingsongxyz.interceptor.MessageConfirmInterceptor;
import com.qingsongxyz.interceptor.OnlineInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * websocket配置类
 */
@RefreshScope
@Configuration
@EnableWebSocketMessageBroker
public class MyWebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Autowired
    private OnlineInterceptor onlineInterceptor;

    @Autowired
    private MessageConfirmInterceptor messageConfirmInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS()
                .setStreamBytesLimit(1 * 1024 * 1024) // 设置流传输最大字节数为1M
                .setHttpMessageCacheSize(1000) // http缓存时间为1s
                .setDisconnectDelay(30 * 1000); // 断开连接后的延长时间
    }

    /**
     * 配置接受客户端发送消息的通道
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(onlineInterceptor, messageConfirmInterceptor); //添加自定义拦截器
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(onlineInterceptor, messageConfirmInterceptor); //添加自定义拦截器
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app") //客户端发送消息给服务器的前缀
                .enableStompBrokerRelay("/exchange", "/queue", "/amq/queue", "/topic", "/temp-queue/") //Stomp代理前綴
                .setVirtualHost(virtualHost)
                .setRelayHost(host)
                .setClientLogin(username) //客户端连接时用户名
                .setClientPasscode(password) //客户端连接时密码
                .setSystemLogin(username) //服务器连接时用户名
                .setSystemPasscode(password); //服务器连接时密码
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        //registry.addDecoratorFactory(handler -> );
    }
}
