package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.OSSFeignServiceImpl;
import org.hibernate.validator.constraints.URL;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

@FeignClient(value = "GarbageClassificationSystem-third-party", contextId = "OSSFeignService", qualifier = "oSSFeignService", fallback = OSSFeignServiceImpl.class)
public interface OSSFeignService {

    @PostMapping("/oss/url")
    CommonResult downloadUrlImage(@NotBlank(message = "图片名称不能为空")
                                  @RequestParam("imageName") String imageName,
                                  @NotBlank(message = "图片url不能为空")
                                  @URL(message = "图片地址必须为正确的url")
                                  @RequestParam("url") String url,
                                  @NotBlank(message = "图片类型不能为空")
                                  @RequestParam("type") String type);
}
