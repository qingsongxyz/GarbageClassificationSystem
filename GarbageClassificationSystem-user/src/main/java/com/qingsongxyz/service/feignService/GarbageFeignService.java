package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.GarbageFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

@FeignClient(value = "GarbageClassificationSystem-garbage", contextId = "GarbageFeignService", qualifier = "garbageFeignService", fallback = GarbageFeignServiceImpl.class)
public interface GarbageFeignService {

    @GetMapping("/garbage/keyword")
    CommonResult getAllGarbageByKeyword(@RequestParam("keyword") String keyword,  @RequestParam(value = "isHighlight", required = false, defaultValue = "true") String isHighlight);
}
