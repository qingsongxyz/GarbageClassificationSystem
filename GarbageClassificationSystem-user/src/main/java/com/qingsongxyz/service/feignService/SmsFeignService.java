package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.SmsFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "GarbageClassificationSystem-third-party", contextId = "SmsFeignService", qualifier = "smsFeignService", fallback = SmsFeignServiceImpl.class)
public interface SmsFeignService {

    @PostMapping("/sms/send")
    CommonResult sendSms(@RequestParam("phone") String phone);
}
