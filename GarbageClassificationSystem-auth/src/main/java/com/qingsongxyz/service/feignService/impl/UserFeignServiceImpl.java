package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.UserFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignServiceImpl implements UserFeignService {

    @Override
    public CommonResult getUserByUsername(String username) {
        log.info("执行getUserByUsername熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }

    @Override
    public CommonResult getUserByPhone(String phone) {
        log.info("执行getUserByPhone熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }

    @Override
    public CommonResult getUserByOpenid(String openid) {
        log.info("执行getUserByOpenid熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }

    @Override
    public CommonResult getUserByAlipayid(String alipayid) {
        log.info("执行getUserByAlipayid熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "服务不可用, 请稍后重试!!!");
    }
}
