package com.qingsongxyz.pojo;

import lombok.*;
import lombok.experimental.Accessors;

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
public class EmailCaptcha {

    @Min(message = "用户id必须为大于0的数字", value = 1)
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotBlank(message = "用户邮箱不能为空")
    @Pattern(message = "请输入合法的邮箱格式", regexp = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
