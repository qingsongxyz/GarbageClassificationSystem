package com.qingsongxyz.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Message;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.feignService.GetTokenFeignService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.validation.BindingGroup;
import com.qingsongxyz.validation.RegisterGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.qingsongxyz.config.rabbitmq.WebSocketQueryConfig.*;

@Api(tags = "websocket测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/webSocket")
public class WebSocketController {

    private static final String COLLECTION_NAME = "messageList";

    private final MongoTemplate mongoTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final GetTokenFeignService getTokenFeignService;

    private final UserService userService;

    public WebSocketController(MongoTemplate mongoTemplate, RabbitTemplate rabbitTemplate, @Qualifier("getTokenFeignService") GetTokenFeignService getTokenFeignService, UserService userService) {
        this.mongoTemplate = mongoTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.getTokenFeignService = getTokenFeignService;
        this.userService = userService;
    }

    @ApiOperation("测试注册三方登录用户消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/addUser")
    public CommonResult addUser(@Validated({RegisterGroup.class}) @RequestBody Message<UserSerializableDTO> message) {
        if (!"THIRD_PARTY_REGISTER".equals(message.getType())) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "传递消息类型不正确!!!");
        }
        log.info("addUser user:{}", message.toString());
        UserSerializableDTO user = message.getContent();
        User u = BeanUtil.copyProperties(user, User.class);

        int result = userService.addUser(u);
        if (result > 0) {
            UserDTO userDTO = userService.getUserByUsername(user.getUsername());
            if (userDTO.getAccountLocked() == 1) {
                throw new LockedException("该账号已被锁定!!!");
            }
            UserSerializableDTO temp = BeanUtil.copyProperties(userDTO, UserSerializableDTO.class);
            temp.setRoleList(userDTO.getRoleList()); //设置生成用户的角色集合
            CommonResult res = getTokenFeignService.loginByThirdParty(temp);
            if(res.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                return res;
            }else {
                return CommonResult.ok(res.getData(), "注册三方登录用户消息成功!");
            }
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "注册三方登录用户消息失败!!!");
    }

    @ApiOperation("测试绑定用户三方消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/bindingUser")
    public CommonResult bindingUser(@Validated({BindingGroup.class}) @RequestBody Message<UserSerializableDTO> message) {
        if (!"THIRD_PARTY_BINDING".equals(message.getType())) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "传递消息类型不正确!!!");
        }

        log.info("bindingUser message:{}", message.toString());
        UserSerializableDTO user = message.getContent();

        //验证用户名、密码
        UserDTO temp = userService.getUserByUsername(user.getUsername());
        user.setId(temp.getId());
        user.setRoleList(temp.getRoleList());
        CommonResult res = getTokenFeignService.loginByBindingUser(user);
        if(res.getCode() == HttpStatus.HTTP_UNAVAILABLE){
            return res;
        }

        Object data = res.getData();
        if (ObjectUtil.isNull(data)) {
            return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "用户名或密码错误, 绑定失败!!!");
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", temp.getId());
        if (StrUtil.isNotBlank(user.getOpenid())) {
            updateWrapper.set("openid", user.getOpenid());
        }
        if (StrUtil.isNotBlank(user.getAlipayid())) {
            updateWrapper.set("alipayid", user.getAlipayid());
        }
        boolean result = userService.update(updateWrapper);
        if (result) {
            return CommonResult.ok(data, "绑定用户三方消息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "绑定用户三方消息失败!!!");
    }

    @ApiOperation("测试推送三方登录用户信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/pushMessage/thirdParty")
    public CommonResult pushThirdPartyMessage(@Validated @RequestBody Message<UserSerializableDTO> message) {
        log.info("pushMessage message:{}", message.toString());
        String json = JSON.toJSONString(message);
        rabbitTemplate.convertAndSend(
                THIRD_PARTY_EXCHANGE_NAME,
                THIRD_PARTY_ROUTING_KEY_PREFIX + "." + message.getTarget(),
                json,
                msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                },
                new CorrelationData(IdUtil.fastSimpleUUID()));

        return CommonResult.ok("推送三方登录用户信息成功!");
    }

    @ApiOperation("测试发送聊天消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/chat")
    public CommonResult chat(@Validated @RequestBody Message<String> message) {
        if (!"CHAT".equals(message.getType())) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "传递消息类型不正确!!!");
        }
        log.info("chat message:{}", message.toString());
        try {
            //保存聊天消息
            Message<String> result = mongoTemplate.insert(message, COLLECTION_NAME);
            String json = JSON.toJSONString(result);
            rabbitTemplate.convertAndSend(
                    DEFAULT_TOPIC_EXCHANGE_NAME,
                    CHAT_ROUTING_KEY_PREFIX + "." + message.getTarget(),
                    json,
                    msg -> {
                        msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return msg;
                    },
                    new CorrelationData(IdUtil.fastSimpleUUID()));
            return CommonResult.ok("发送聊天消息成功!");
        } catch (AmqpException e) {
            log.error("chatToUser exception:{}", e.getMessage());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "发送聊天消息失败!!!");
    }
}

