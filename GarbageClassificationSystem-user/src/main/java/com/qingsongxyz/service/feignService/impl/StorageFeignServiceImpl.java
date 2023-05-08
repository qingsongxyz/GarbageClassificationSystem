package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.StorageFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StorageFeignServiceImpl implements StorageFeignService {

    @Override
    public CommonResult decreaseStorage(long goodId, int number) {
        log.info("执行decreaseStorage熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
