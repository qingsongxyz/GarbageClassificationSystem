package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserCaptcha extends Model<UserCaptcha> {

    @Valid
    private User user;

    @NotNull(message = "验证码校验对象不能为空")
    private CaptchaResult captchaResult;
}
