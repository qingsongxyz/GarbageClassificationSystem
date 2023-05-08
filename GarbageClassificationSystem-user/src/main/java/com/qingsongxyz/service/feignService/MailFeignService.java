package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Mail;
import com.qingsongxyz.service.feignService.impl.MailFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "GarbageClassificationSystem-third-party", contextId = "MailFeignService", qualifier = "mailFeignService", fallback = MailFeignServiceImpl.class)
public interface MailFeignService {

    @PostMapping("/mail/message")
    CommonResult mail(@RequestBody Mail mail);
}
