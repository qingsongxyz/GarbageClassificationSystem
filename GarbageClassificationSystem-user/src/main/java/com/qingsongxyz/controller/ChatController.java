package com.qingsongxyz.controller;

import cn.hutool.core.util.ObjectUtil;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Message;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.vo.UserVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.qingsongxyz.constant.RedisConstant.ONLINE_CHAT_USERS;

@Slf4j
@RestController
@Validated
@RequestMapping("/chat")
@Api(tags = "聊天类测试")
public class ChatController {

    private final StringRedisTemplate stringRedisTemplate;

    private final UserService userService;

    private final MongoTemplate mongoTemplate;

    public ChatController(StringRedisTemplate stringRedisTemplate, UserService userService, MongoTemplate mongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
    }

    @ApiOperation("测试查询所有聊天在线用户")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/userList/online")
    public CommonResult getOnlineUserList() {
        Set<String> onlineUserList = stringRedisTemplate.opsForSet().members(ONLINE_CHAT_USERS);
        return CommonResult.ok(onlineUserList, "查询所有聊天在线用户成功!");
    }

    @ApiOperation("测试查询所有管理员用户")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/userList/admin")
    public CommonResult getAdminList() {
        List<UserVO> adminList = userService.getAdminList();
        return CommonResult.ok(adminList, "查询所有管理员用户成功!");
    }

    @ApiOperation("测试查询和用戶聊天的相关用户列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/userList/relative")
    public CommonResult getChatRelativeUserList(@NotNull(message = "用户id不能为空")
                                                @Min(message = "用户id必须为大于0的数字", value = 1) Long userId) {
        ArrayList<UserVO> userVOList = new ArrayList<>();
        List<String> sourceList = mongoTemplate.findDistinct(new Query(Criteria.where("target").is(userId.toString())), "source", Message.class, String.class);
        sourceList.forEach(s -> {
            UserVO userVO = userService.getUserById(Long.parseLong(s));
            userVOList.add(userVO);
        });
        return CommonResult.ok(userVOList, "查询和用戶聊天的相关用户列表成功!");
    }

    @ApiOperation("测试查询两个用户之间未确认消息的数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "发送用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "target", value = "接受用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/nack/{source}/{target}")
    public CommonResult getNackMessageListCount(@Min(message = "发送用户id必须为大于0的数字", value = 1)
                                                @PathVariable("source") Long source,
                                                @Min(message = "接受用户id必须为大于0的数字", value = 1)
                                                @PathVariable("target") Long target) {
        Criteria criteria = Criteria
                .where("source").is(source)
                .and("target").is(target)
                .and("isAck").is(false);
        long count = mongoTemplate.count(new Query(criteria), Message.class);
        return CommonResult.ok(count, "查询两个用户之间未确认消息的数量成功!");
    }

    @ApiOperation("测试查询两个用户最近未确认的消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "发送用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "target", value = "接受用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/messageList/recent/{source}/{target}")
    public CommonResult getRecentMessageList(@Min(message = "用户id必须为大于0的数字", value = 1)
                                             @PathVariable("source") Long source,
                                             @Min(message = "用户id必须为大于0的数字", value = 1)
                                             @PathVariable("target") Long target) {
        //两个用户之间未确认的消息条件
        Criteria criteria = Criteria
                .where("target").is(source.toString())
                .and("source").is(target.toString())
                .and("ack").is(false);
        //按时间升序排序
        Query query = new Query(criteria).with(Sort.by(Sort.Order.asc("time")));
        List<Message> messageList = mongoTemplate.find(query, Message.class);

        if (ObjectUtil.isNotEmpty(messageList)) {
            messageList.forEach(m -> {
                Update update = new Update();
                update.set("ack", true);
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(m.getId())), update, Message.class);
            });
        }
        return CommonResult.ok(messageList, "查询两个用户最近的消息列表成功!");
    }
}

