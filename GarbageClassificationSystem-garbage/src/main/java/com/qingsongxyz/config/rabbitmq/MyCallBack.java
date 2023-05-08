package com.qingsongxyz.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private final RabbitTemplate rabbitTemplate;

    public MyCallBack(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init(){
        //将重写后的确认回调和回退消息回调注入rabbitTemplate
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 交换机对生产者的消息产生应答
     * @param correlationData 回调消息的id和信息,需要生产者发送
     * @param b               交换机是否收到消息  true
     * @param s               失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (b) {
            log.info("交换机收到id为{}的消息", id);
        } else {
            log.error("交换机未收到id为{}的消息,原因:{}", id, s);
        }
    }

    /**
     * 队列未收到交换机的消息时将消息回退给生产者
     * @param message 消息内容
     * @param i 响应码
     * @param s 响应结果
     * @param s1 交换机名称
     * @param s2 路由键
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.error("消息{}被交换机{}退回,路由:{},响应码:{},原因是:{}", message, s1, s2, i, s);
    }
}