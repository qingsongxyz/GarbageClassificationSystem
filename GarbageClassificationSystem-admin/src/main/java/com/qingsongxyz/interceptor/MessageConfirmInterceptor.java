package com.qingsongxyz.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.qingsongxyz.config.rabbitmq.WebSocketQueryConfig.*;

@Slf4j
@Component
public class MessageConfirmInterceptor implements ChannelInterceptor {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MongoTemplate mongoTemplate;

    public MessageConfirmInterceptor(@Lazy SimpMessagingTemplate simpMessagingTemplate, MongoTemplate mongoTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        //对于非stomp消息类型(eg.心跳检测)直接放行
        if (ObjectUtil.isNull(command)) {
            return message;
        }

        if (StompCommand.ACK.equals(command) || StompCommand.NACK.equals(command)) {
            List<String> body = headerAccessor.getNativeHeader("body");
            if (ObjectUtil.isNotEmpty(body)) {
                String json = body.get(0);
                if (StrUtil.isNotBlank(json)) {
                    com.qingsongxyz.pojo.Message msg = JSON.parseObject(json, com.qingsongxyz.pojo.Message.class);
                    String type = msg.getType();
                    String target = msg.getTarget();

                    switch (command) {
                        case ACK:
                            log.info("消息-{}-被ack确认", json);
                            if ("CHAT".equals(type)) {
                                //修改消息状态为已确认
                                ObjectId id = new ObjectId(msg.getId());
                                Update update = new Update();
                                update.set("ack", true);
                                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),
                                        update,
                                        com.qingsongxyz.pojo.Message.class);
                            }
                            break;
                        case NACK:
                            log.info("消息-{}-被nack拒绝", json);
                            //消息未确认进行重传
                            if ("CHAT".equals(type)) {
                                simpMessagingTemplate.convertAndSend("/topic/" + CHAT_ROUTING_KEY_PREFIX + "." + target, json);
                            } else {
                                simpMessagingTemplate.convertAndSend("/exchange/" + THIRD_PARTY_EXCHANGE_NAME + "/" + THIRD_PARTY_ROUTING_KEY_PREFIX + "." + target, json);
                            }
                            break;
                    }
                }
            }
        }
        return message;
    }
}
