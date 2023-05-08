package com.qingsongxyz.pojo;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    @NotNull(message = "用户id不能为空")
    @Min(message = "用户id必须为大于0的数字", value = 1)
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20)
    private String username;

    @NotBlank(message = "用户邮箱不能为空")
    @Pattern(message = "请输入合法的邮箱格式", regexp = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}")
    private String targetMail;

    @Range(max = 1, message = "请输入正确的发送信息类型")
    private int letterType;

}
