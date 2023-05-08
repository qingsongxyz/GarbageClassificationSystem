package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.CaptchaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RefreshScope
@Api(tags = "验证码类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/captcha")
public class CaptchaController {

    @Value("${captcha.id}")
    private String captchaId;

    @Value("${captcha.key}")
    private String captchaKey;

    @Value("${captcha.domain}")
    private String domain;

    @ApiOperation("测试captcha验证码二次验证")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/check")
    public CommonResult checkTwice(@RequestBody CaptchaResult captchaResult) {
        log.info("CaptchaController checkTwice:{}", captchaResult);
        String signToken = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, captchaKey).hmacHex(captchaResult.getLot_number());

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lot_number", captchaResult.getLot_number());
        queryParams.add("captcha_output", captchaResult.getCaptcha_output());
        queryParams.add("pass_token", captchaResult.getPass_token());
        queryParams.add("gen_time", captchaResult.getGen_time());
        queryParams.add("sign_token", signToken);
        String url = String.format(domain + "/validate" + "?captcha_id=%s", captchaId);

        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, Object> map = null;

        try {
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);
            ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
            String resBody = response.getBody();
            JSONObject jsonObject = JSON.parseObject(resBody);
            map = jsonObject.getInnerMap();
            if ("success".equals(map.get("result"))) {
                return CommonResult.ok(map, "验证码验证成功!");
            } else {
                return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, map, "验证码验证失败!!!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, map, "验证码验证失败!!!");
    }
}
