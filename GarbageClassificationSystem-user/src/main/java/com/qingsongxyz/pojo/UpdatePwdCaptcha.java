package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePwdCaptcha extends Model<UpdatePwdCaptcha> {

    @NotNull(message = "用户id不能为空")
    @Min(message = "用户id必须为大于0的数字", value = 1)
    private Long id;

    @NotBlank(message = "旧密码不能为空")
    @Length(message = "旧密码长度必须在1~60之间", min = 1, max = 60)
    private String oldPwd;

    @NotBlank(message = "新密码不能为空")
    @Length(message = "新密码长度必须在1~60之间", min = 1, max = 60)
    private String newPwd;

    @NotNull(message = "验证码校验对象不能为空")
    private CaptchaResult captchaResult;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
