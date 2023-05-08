package com.qingsongxyz.service.feignService;

import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Message;
import com.qingsongxyz.service.feignService.impl.PushThirdPartyMessageFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "GarbageClassificationSystem-admin", contextId = "PushThirdPartyMessageFeignService", qualifier = "pushThirdPartyMessageFeignService", fallback = PushThirdPartyMessageFeignServiceImpl.class)
public interface PushThirdPartyMessageFeignService {

    @PostMapping("/webSocket/pushMessage/thirdParty")
    CommonResult pushThirdPartyMessage(@RequestBody Message<UserSerializableDTO> message);
}
