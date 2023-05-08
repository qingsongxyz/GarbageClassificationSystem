package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TokenCaptcha extends Model<TokenCaptcha> {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "验证码校验对象不能为空")
    private CaptchaResult captchaResult;

    @Override
    public Serializable pkVal() {
        return username;
    }
}
