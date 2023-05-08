package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.feignService.GetTokenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetTokenFeignServiceImpl implements GetTokenFeignService {

    @Override
    public CommonResult loginByThirdParty(UserSerializableDTO userDTO) {
        log.info("执行loginByThirdParty熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }

    @Override
    public CommonResult loginByBindingUser(UserSerializableDTO userDTO) {
        log.info("执行loginByBindingUser熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
