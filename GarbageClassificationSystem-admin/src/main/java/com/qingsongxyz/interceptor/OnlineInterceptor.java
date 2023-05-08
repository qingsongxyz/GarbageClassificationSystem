package com.qingsongxyz.interceptor;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.qingsongxyz.constant.RedisConstant.ONLINE_CHAT_USERS;

/**
 * 用户聊天上下线拦截器
 */
@Slf4j
@Component
public class OnlineInterceptor implements ChannelInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public OnlineInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        //对于非stomp消息类型(eg.心跳检测)直接放行
        if (ObjectUtil.isNull(command)) {
            return message;
        }

        if (StompCommand.CONNECT.equals(command) || StompCommand.DISCONNECT.equals(command)) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                Object id = ((Map) raw).get("id");
                Object type = ((Map) raw).get("type");
                if (ObjectUtil.isNotEmpty(id) && ObjectUtil.isNotEmpty(type)) {
                    id = ((LinkedList) id).get(0);
                    type = ((LinkedList) type).get(0);
                    if ("CHAT".equals(type)) {
                        //获取sessionId
                        String sessionId = "";
                        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                        if (ObjectUtil.isNotEmpty(sessionAttributes)) {
                            sessionId = sessionAttributes.get("sessionId").toString();
                        }
                        switch (command) {
                            case CONNECT:
                                //用户聊天上线
                                log.info("用户:{}, sessionId:{}, 聊天上线", id, sessionId);
                                stringRedisTemplate.opsForSet().add(ONLINE_CHAT_USERS, id.toString());
                                break;
                            case DISCONNECT:
                                //用户聊天下线
                                log.info("用户:{}, sessionId:{}, 聊天下线", id, sessionId);
                                stringRedisTemplate.opsForSet().remove(ONLINE_CHAT_USERS, id.toString());
                                break;
                        }
                    }
                }
            }
        }
        return message;
    }
}
