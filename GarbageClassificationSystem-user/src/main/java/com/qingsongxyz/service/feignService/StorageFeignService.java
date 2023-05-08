package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.StorageFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "GarbageClassificationSystem-mall", contextId = "StorageFeignService", qualifier = "storageFeignService", fallback = StorageFeignServiceImpl.class)
public interface StorageFeignService {

    @PostMapping("/storage/decrease/{goodId}")
    CommonResult decreaseStorage(@RequestParam("goodId") @PathVariable("goodId") long goodId, @RequestParam("number") int number);
}
