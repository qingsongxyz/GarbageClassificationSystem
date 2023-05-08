package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CaptchaResult;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.CaptchaFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "GarbageClassificationSystem-third-party", contextId = "CaptchaFeignService", qualifier = "captchaFeignService", fallback = CaptchaFeignServiceImpl.class)
public interface CaptchaFeignService {

    @PostMapping("/captcha/check")
    CommonResult checkTwice(@RequestBody CaptchaResult captchaResult);
}
