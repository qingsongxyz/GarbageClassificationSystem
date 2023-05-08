package com.qingsongxyz.service.feignService;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.impl.ClassifyingRubbishFeignServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "GarbageClassificationSystem-third-party", contextId = "ClassifyingRubbishFeignService", qualifier = "classifyingRubbishFeignService", fallback = ClassifyingRubbishFeignServiceImpl.class)
public interface ClassifyingRubbishFeignService {

    @PostMapping(value = "/classify-rubbish/recognize/image", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    CommonResult recognizeImage(@RequestPart("file") MultipartFile file);

    @PostMapping("/classify-rubbish/recognize/url")
    CommonResult recognizeUrl(@RequestParam("url") String url);
}
