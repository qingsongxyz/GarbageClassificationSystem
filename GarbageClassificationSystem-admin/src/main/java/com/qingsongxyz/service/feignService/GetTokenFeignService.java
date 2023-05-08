package com.qingsongxyz.service.feignService;

import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.GetTokenFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "GarbageClassificationSystem-auth", contextId = "GetTokenFeignService", qualifier = "getTokenFeignService", fallback = GetTokenFeignServiceImpl.class)
public interface GetTokenFeignService {

    @PostMapping("/oauth/token/thirdParty")
    CommonResult loginByThirdParty(@Validated @RequestBody UserSerializableDTO userDTO);

    @PostMapping("/oauth/token/binding/thirdParty")
    CommonResult loginByBindingUser(@Validated @RequestBody UserSerializableDTO userDTO);
}
