package com.qingsongxyz.pojo;

import lombok.*;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PhoneCaptcha {

    @NotBlank(message = "手机号不能为空")
    @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
