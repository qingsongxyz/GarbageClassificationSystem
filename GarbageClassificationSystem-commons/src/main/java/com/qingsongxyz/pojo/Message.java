package com.qingsongxyz.pojo;

import com.qingsongxyz.constraints.MessageTypeAnnotation;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * websocket消息类
 * @param <T> 传递消息内容类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Message对象", description = "消息表")
@Document(value = "messageList") //文档名称为messageList
public class Message<T> {

    @Id //唯一id
    private String id;

    @Field
    @MessageTypeAnnotation
    @NotBlank(message = "消息类型不能为空")
    private String type;

    //来源
    @Field
    @NotBlank(message = "消息来源不能为空")
    private String source;

    //目的地
    @Field
    @NotBlank(message = "消息目的地不能为空")
    private String target;

    //内容
    @Field
    @NotNull(message = "消息内容不能为空")
    @Valid
    private T content;

    @Field
    @AssertFalse(message = "消息初始状态必须为未确认")
    private Boolean ack;

    @Field
    @NotNull(message = "发送时间不能为空")
    private LocalDateTime time;

    @Version //乐观锁
    private Long version;

    @Getter
    @AllArgsConstructor
    public enum MessageType {

        THIRD_PARTY_REGISTER(1, "注册三方用户消息"),

        THIRD_PARTY_BINDING(2, "绑定三方用户消息"),

        THIRD_PARTY_SUCCESS(3, "三方登录成功消息"),

        CHAT(4, "聊天消息");

        private int id;

        private String value;
    }
}
