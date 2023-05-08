package com.qingsongxyz.service.feignService.impl;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Message;
import com.qingsongxyz.service.feignService.PushThirdPartyMessageFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushThirdPartyMessageFeignServiceImpl implements PushThirdPartyMessageFeignService {

    @Override
    public CommonResult pushThirdPartyMessage(Message<UserSerializableDTO> message) {
        log.info("执行pushThirdPartyMessage熔断方法...");
        return CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务不可用, 请稍后重试!!!");
    }
}
