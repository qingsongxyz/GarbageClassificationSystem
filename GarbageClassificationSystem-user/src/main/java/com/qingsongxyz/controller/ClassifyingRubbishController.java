package com.qingsongxyz.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.qingsongxyz.constraints.Image;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.ClassifyingRubbishFeignService;
import com.qingsongxyz.service.feignService.GarbageFeignService;
import com.qingsongxyz.vo.GarbageVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 垃圾分类识别前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Api(tags = "垃圾分类识别测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/classify-rubbish")
public class ClassifyingRubbishController {

    private final ClassifyingRubbishFeignService classifyingRubbishFeignService;

    private final GarbageFeignService garbageFeignService;

    public ClassifyingRubbishController(ClassifyingRubbishFeignService classifyingRubbishFeignService, @Qualifier("garbageFeignService") GarbageFeignService garbageFeignService) {
        this.classifyingRubbishFeignService = classifyingRubbishFeignService;
        this.garbageFeignService = garbageFeignService;
    }

    @ApiOperation("测试垃圾分类图像识别(文件)")
    @ApiImplicitParam(name = "file", value = "图片文件", required = true, paramType = "form", dataTypeClass = MultipartFile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/recognize/image")
    public CommonResult recognizeImage(@Image MultipartFile file) throws Exception {
        CommonResult result = classifyingRubbishFeignService.recognizeImage(file);
        if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
            return result;
        }
        return getGarbageInfo(result);
    }

    @ApiOperation("测试垃圾分类图像识别(url)")
    @ApiImplicitParam(name = "url", value = "图片url", required = true, paramType = "query", dataTypeClass = String.class, example = "http://baidu.com/1.png")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/recognize/url")
    public CommonResult recognizeUrl(@NotBlank(message = "图片url不能为空")
                                     @URL(message = "垃圾图片必须为正确的url") String url) throws Exception {
        CommonResult result = classifyingRubbishFeignService.recognizeUrl(url);
        if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
            return result;
        }
        return getGarbageInfo(result);
    }

    /**
     * 通过垃圾图片识别出垃圾信息
     *
     * @param result 图片识别结果
     * @return 垃圾信息
     */
    private CommonResult getGarbageInfo(CommonResult result) {
        Object data = result.getData();
        if (ObjectUtil.isNotNull(data)) {
            if (data instanceof LinkedHashMap) {
                Object value = ((LinkedHashMap) data).get("elements");
                List<LinkedHashMap> elements = (List<LinkedHashMap>) value;
                LinkedHashMap map = elements.get(0);
                String rubbish = map.get("rubbish").toString();
                CommonResult res = garbageFeignService.getAllGarbageByKeyword(rubbish, "false");
                if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                    return res;
                }

                Object o = res.getData();
                if(o instanceof List) {
                    List<LinkedHashMap> maps = (List<LinkedHashMap>)o;
                    if (ObjectUtil.isNotEmpty(maps)) {
                        LinkedHashMap linkedHashMap = maps.get(0);
                        GarbageVO garbageVO = BeanUtil.fillBeanWithMap(linkedHashMap, new GarbageVO(), CopyOptions.create().ignoreNullValue().setIgnoreError(true));
                        return CommonResult.ok(garbageVO, "垃圾分类图像识别成功!");
                    }
                    return CommonResult.failure(HttpStatus.HTTP_NOT_FOUND, "垃圾不存在, 请联系管理员进行处理!!!");
                }
            }
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "垃圾分类图像识别失败!!!");
    }
}
