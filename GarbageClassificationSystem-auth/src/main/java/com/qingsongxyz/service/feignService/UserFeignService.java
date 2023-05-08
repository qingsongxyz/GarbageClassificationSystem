package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.UserFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "GarbageClassificationSystem-admin", contextId = "UserFeignService", qualifier = "userFeignService", fallback = UserFeignServiceImpl.class)
public interface UserFeignService {

    @GetMapping("/user/name")
    CommonResult getUserByUsername(@RequestParam("username") String username);

    @GetMapping("/user/phone")
    CommonResult getUserByPhone(@RequestParam("phone") String phone);

    @GetMapping("/user/openid")
    CommonResult getUserByOpenid(@RequestParam("openid") String openid);

    @GetMapping("/user/alipayid")
    CommonResult getUserByAlipayid(@RequestParam("alipayid") String alipayid);
}
