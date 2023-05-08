package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.LetterType;
import com.qingsongxyz.pojo.Mail;
import com.qingsongxyz.template.Template;
import com.qingsongxyz.util.CaptchaUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;
import static com.qingsongxyz.constant.RedisConstant.CAPTCHA_KEY;
import static com.qingsongxyz.constant.RedisConstant.CAPTCHA_TTL_SECOND;

/**
 * <p>
 * 邮件类 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@RefreshScope
@Api(tags = "发送邮件测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/mail")
public class MailController {

    private final JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String hostname;

    private final StringRedisTemplate stringRedisTemplate;

    public MailController(JavaMailSenderImpl mailSender, StringRedisTemplate stringRedisTemplate) {
        this.mailSender = mailSender;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 发送简单邮件
     */
    @ApiOperation("测试发送邮件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/message")
    public CommonResult mail(@RequestBody @Validated Mail mail) {

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            LetterType[] letterTypes = LetterType.values();
            LetterType type = null;
            for (LetterType t : letterTypes) {
                if (t.getId() == mail.getLetterType()) {
                    type = t;
                }
            }

            helper.setSubject(type.getName()); //主题

            //生成验证码 并存入redis
            String code = CaptchaUtil.create();
            stringRedisTemplate.opsForValue().set(CAPTCHA_KEY + ":" + mail.getTargetMail(), code, CAPTCHA_TTL_SECOND, TimeUnit.SECONDS);
            String msg = Template.generateCaptchaTemplate(mail.getUsername(), code);

            helper.setText(msg, true); //正文
            helper.setFrom(new InternetAddress(hostname, "管理员")); //自己的qq邮箱
            helper.setTo(mail.getTargetMail()); //收件人的qq邮箱

            mailSender.send(message);
            return CommonResult.ok("邮件发送成功!");
        } catch (Exception e) {
            log.error("MailController mail:{}", e.getMessage());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "邮件发送失败!!!");
    }
}
