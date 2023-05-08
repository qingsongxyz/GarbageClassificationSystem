package com.qingsongxyz.controller;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.GarbageDeliveryDetailsService;
import com.qingsongxyz.vo.GarbageDeliveryDetailsVO;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>
 * 垃圾投递详情表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Api(tags = "垃圾投递详情类测试")
@Validated //非pojo参数检验声明
@RestController
@RequestMapping("/garbage-delivery-details")
public class GarbageDeliveryDetailsController {

    private final GarbageDeliveryDetailsService garbageDeliveryDetailsService;

    public GarbageDeliveryDetailsController(GarbageDeliveryDetailsService garbageDeliveryDetailsService) {
        this.garbageDeliveryDetailsService = garbageDeliveryDetailsService;
    }

    @ApiOperation("测试通过垃圾投递id查询垃圾投递详情信息")
    @ApiImplicitParam(name = "deliveryId", value = "垃圾投递信息id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/{deliveryId}")
    public CommonResult getGarbageDeliveryDetailsVOByDeliveryId(@Min(value = 1, message = "垃圾投递信息id必须是大于0的数字") @PathVariable("deliveryId") long deliveryId){
        List<GarbageDeliveryDetailsVO> garbageDeliveryDetailsVOList = garbageDeliveryDetailsService.getGarbageDeliveryDetailsListByDeliveryId(deliveryId);
        return CommonResult.ok(garbageDeliveryDetailsVOList, "通过垃圾投递id查询垃圾投递详情信息成功!");
    }
}
