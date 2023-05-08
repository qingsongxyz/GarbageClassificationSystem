package com.qingsongxyz.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.PhoneCaptcha;
import com.qingsongxyz.pojo.TokenCaptcha;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.feignService.UserFeignService;
import com.qingsongxyz.service.impl.AuthServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotBlank;
import java.util.*;
import static com.qingsongxyz.constant.RedisConstant.*;

@RefreshScope
@RestController
@Slf4j
@RequestMapping("/oauth")
public class AuthController {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    private final AuthServiceImpl authServiceImpl;

    private final UserFeignService userFeignService;

    private final StringRedisTemplate stringRedisTemplate;

    public AuthController(AuthServiceImpl authServiceImpl, UserFeignService userFeignService, StringRedisTemplate stringRedisTemplate) {
        this.authServiceImpl = authServiceImpl;
        this.userFeignService = userFeignService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @ApiOperation("测试通过用户名密码和验证码登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/token")
    public CommonResult postAccessTokenWithCaptchaResult(@Validated @RequestBody TokenCaptcha tokenCaptcha) {

        OAuth2AccessToken accessToken;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", tokenCaptcha.getUsername());
        parameters.put("password", tokenCaptcha.getPassword());
        parameters.put("grant_type", "password");

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, new ArrayList<>());
        try {
            accessToken = authServiceImpl.postAccessTokenWithCaptchaResult(tokenCaptcha.getCaptchaResult(), principal, parameters);
            return CommonResult.ok(accessToken, "用户名密码和验证码登录成功!");
        } catch (HttpRequestMethodNotSupportedException e) {
            log.error("AuthController postAccessToken HttpRequestMethodNotSupportedException:{}", e.getMessage());
        } catch (Exception e) {
            log.error("AuthController postAccessToken exception:{}", e.toString());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "用户名密码和验证码登录失败!!!");
    }

    @ApiOperation("测试通过手机号和验证码登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @SentinelResource()
    @PostMapping("/token/phone")
    public CommonResult loginByPhone(@Validated @RequestBody PhoneCaptcha phoneCaptcha) {
        String phone = phoneCaptcha.getPhone();
        String captcha = phoneCaptcha.getCaptcha();
        //1.校验验证码是否一致
        String code = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY + ":" + phone);
        if (!captcha.equals(code)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "手机号或验证码错误!!!");
        }

        //2.远程调用 通过手机号查询用户信息
        CommonResult result = userFeignService.getUserByPhone(phone);
        if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
            return result;
        }

        Object data = result.getData();
        if (ObjectUtil.isNull(data)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "手机号未绑定!!!");
        }

        if (data instanceof Map) {
            UserDTO userDTO = BeanUtil.fillBeanWithMap((Map) data, new UserDTO(), CopyOptions.create().ignoreNullValue().ignoreError());
            if (userDTO.getAccountLocked() == 1) {
                throw new LockedException("该账号已被锁定!!!");
            }

            UserSerializableDTO userSerializableDTO = BeanUtil.copyProperties(userDTO, UserSerializableDTO.class);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("user", JSON.toJSONString(userSerializableDTO));
            parameters.put("grant_type", "my");

            //3.生成jwt token
            OAuth2AccessToken accessToken;
            UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, new ArrayList<>());
            try {
                accessToken = authServiceImpl.postAccessToken(principal, parameters);

                //4.添加附加信息
                DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                Map<String, Object> additionalMap = new HashMap<>();
                additionalMap.put("id", userDTO.getId());
                additionalMap.put("username", userDTO.getUsername());
                additionalMap.put("roleList", userDTO.getRoleList());
                token.setAdditionalInformation(additionalMap);

                return CommonResult.ok(token, "手机号和验证码登录成功!");
            } catch (HttpRequestMethodNotSupportedException e) {
                log.error("AuthController loginByPhone HttpRequestMethodNotSupportedException:{}", e.getMessage());
            } catch (Exception e) {
                log.error("AuthController loginByPhone exception:{}", e.toString());
            }
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "手机号和验证码登录失败!!!");
    }

    @ApiOperation("测试绑定用户三方登录信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/token/binding/thirdParty")
    public CommonResult loginByBindingUser(@RequestBody UserSerializableDTO userDTO) {
        OAuth2AccessToken accessToken;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", userDTO.getUsername());
        parameters.put("password", userDTO.getPassword());
        parameters.put("grant_type", "password");

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, new ArrayList<>());
        try {
            accessToken = authServiceImpl.postAccessToken(principal, parameters);

            //4.添加附加信息
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
            Map<String, Object> map = new HashMap<>();
            map.put("id", userDTO.getId());
            map.put("username", userDTO.getUsername());
            map.put("roleList", userDTO.getRoleList());
            token.setAdditionalInformation(map);

            return CommonResult.ok(accessToken, "绑定用户三方登录信息成功!");
        } catch (HttpRequestMethodNotSupportedException e) {
            log.error("AuthController loginByBindingUser HttpRequestMethodNotSupportedException:{}", e.getMessage());
        } catch (Exception e) {
            log.error("AuthController loginByBindingUser exception:{}", e.toString());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "绑定用户三方登录信息失败!!!");
    }

    @ApiOperation("测试通过三方扫码登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/token/thirdParty")
    public CommonResult loginByThirdParty(@RequestBody UserSerializableDTO userDTO) {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("user", JSON.toJSONString(userDTO));
        parameters.put("grant_type", "my");

        //生成jwt token
        OAuth2AccessToken accessToken;
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, new ArrayList<>());
        try {
            accessToken = authServiceImpl.postAccessToken(principal, parameters);

            //添加附加信息
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
            Map<String, Object> additionalMap = new HashMap<>();
            additionalMap.put("id", userDTO.getId());
            additionalMap.put("username", userDTO.getUsername());
            additionalMap.put("roleList", userDTO.getRoleList());
            token.setAdditionalInformation(additionalMap);

            return CommonResult.ok(token, "通过三方扫码登录成功!");
        } catch (HttpRequestMethodNotSupportedException e) {
            log.error("AuthController loginByThirdParty HttpRequestMethodNotSupportedException:{}", e.getMessage());
        } catch (Exception e) {
            log.error("AuthController loginByThirdParty exception:{}", e.toString());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过三方扫码登录失败!!!");
    }

    @ApiOperation("测试刷新令牌")
    @ApiImplicitParam(name = "refreshToken", value = "刷新令牌", required = true, paramType = "query", dataTypeClass = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/token/refresh")
    public CommonResult refreshToken(@NotBlank(message = "刷新令牌不能为空") String refreshToken) {

        OAuth2AccessToken accessToken;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "refresh_token");
        parameters.put("refresh_token", refreshToken);

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, new ArrayList<>());
        try {
            accessToken = authServiceImpl.postAccessToken(principal, parameters);
            return CommonResult.ok(accessToken, "刷新令牌成功!");
        } catch (HttpRequestMethodNotSupportedException e) {
            e.printStackTrace();
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "刷新令牌失败!!!");
    }
}
