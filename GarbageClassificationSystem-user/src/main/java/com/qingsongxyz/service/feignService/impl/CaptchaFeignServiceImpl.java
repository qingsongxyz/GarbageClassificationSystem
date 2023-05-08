package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CaptchaResult;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.CaptchaFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CaptchaFeignServiceImpl implements CaptchaFeignService {

    @Override
    public CommonResult checkTwice(CaptchaResult captchaResult) {
        log.info("执行checkTwice熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
