package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.SmsFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsFeignServiceImpl implements SmsFeignService {

    @Override
    public CommonResult sendSms(String phone) {
        log.info("执行sendSms熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
