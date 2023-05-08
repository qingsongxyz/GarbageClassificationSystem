package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.GarbageFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GarbageFeignServiceImpl implements GarbageFeignService {

    @Override
    public CommonResult getAllGarbageByKeyword(String keyword, String isHighlight) {
        log.info("执行getAllGarbageByKeyword熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
