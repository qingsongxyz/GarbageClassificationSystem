package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.ClassifyingRubbishFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ClassifyingRubbishFeignServiceImpl implements ClassifyingRubbishFeignService {
    @Override
    public CommonResult recognizeImage(MultipartFile file) {
        log.info("执行recognizeImage熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }

    @Override
    public CommonResult recognizeUrl(String url) {
        log.info("执行recognizeUrl熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
