package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.util.CaptchaUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.concurrent.TimeUnit;
import static com.qingsongxyz.constant.RedisConstant.CAPTCHA_KEY;
import static com.qingsongxyz.constant.RedisConstant.CAPTCHA_TTL_SECOND;

/**
 * <p>
 * 短信服务前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@RefreshScope
@Api(tags = "短信服务测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/sms")
public class SmsController {

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKey;

    @Value("${spring.cloud.alicloud.sms.signName}")
    private String signName;

    @Value("${spring.cloud.alicloud.sms.template-code}")
    private String templateCode;

    private final StringRedisTemplate stringRedisTemplate;

    public SmsController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @ApiOperation("测试发送短信")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, paramType = "query", dataTypeClass = String.class, example = "18327088164")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/send")
    public CommonResult sendSms(@NotBlank(message = "手机号不能为空")
                                @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$") String phone) throws Exception {
        //1.创建客户端
        Config config = new Config()
                .setAccessKeyId(accessId)
                .setAccessKeySecret(accessKey);

        config.endpoint = "dysmsapi.aliyuncs.com";

        Client client = new Client(config);

        try {
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            //设置用户手机号
            sendSmsRequest.setPhoneNumbers(phone);
            //设置短信签名名称
            sendSmsRequest.setSignName(signName);
            //设置短信模板CODE
            sendSmsRequest.setTemplateCode(templateCode);

            //生成随机验证码
            String captcha = CaptchaUtil.create();
            //存入redis
            stringRedisTemplate.opsForValue().set(CAPTCHA_KEY + ":" + phone, captcha, CAPTCHA_TTL_SECOND, TimeUnit.SECONDS);

            String templateCode = "{\"code\":\"" + captcha + "\"}";
            //短信模板中的验证码
            sendSmsRequest.setTemplateParam(templateCode);

            //发送短信 获取状态码
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            SendSmsResponseBody body = sendSmsResponse.getBody();
            String code = body.getCode();
            if ("OK".equals(code)) {
                return CommonResult.ok("发送短信成功!");
            }
        } catch (TeaException e) {
            log.error("SmsController TeaException:{}", e.getMessage());
            com.aliyun.teautil.Common.assertAsString(e.message);
        } catch (Exception _e) {
            log.error("SmsController Exception:{}", _e.getMessage());
            TeaException error = new TeaException(_e.getMessage(), _e);
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "发送短信失败!!!");
    }
}
