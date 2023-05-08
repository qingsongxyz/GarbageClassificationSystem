package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.GarbageDeliveryDetails;
import com.qingsongxyz.service.GarbageDeliveryDetailsService;
import com.qingsongxyz.validation.CreateGroup;
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

    @ApiOperation("测试添加垃圾投递详情信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGarbageDeliveryDetails(@RequestBody @Validated({CreateGroup.class})GarbageDeliveryDetails garbageDeliveryDetails){
        int result = garbageDeliveryDetailsService.addGarbageDeliveryDetails(garbageDeliveryDetails);
        if (result > 0) {
            return CommonResult.ok("添加垃圾投递详情信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加垃圾投递详情信息失败!!!");
    }

    @ApiOperation("测试通过垃圾投递id删除垃圾投递详情信息")
    @ApiImplicitParam(name = "deliveryId", value = "垃圾投递信息id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{deliveryId}")
    public CommonResult deleteGarbageDeliveryDetailsByDeliveryId(@Min(message = "垃圾投递信息id必须大于0", value = 1) @PathVariable("deliveryId") long deliveryId){
        int result = garbageDeliveryDetailsService.deleteGarbageDeliveryDetailsByDeliveryId(deliveryId);
        if (result > 0) {
            return CommonResult.ok("通过垃圾投递id删除垃圾投递详情信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过垃圾投递id删除垃圾投递详情信息失败!!!");
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
    public CommonResult getGarbageDeliveryDetailsVOByDeliveryId(@Min(message = "垃圾投递信息id必须大于0", value = 1) @PathVariable("deliveryId") long deliveryId){
        List<GarbageDeliveryDetailsVO> garbageDeliveryDetailsVOList = garbageDeliveryDetailsService.getGarbageDeliveryDetailsVOByDeliveryId(deliveryId);
        return CommonResult.ok(garbageDeliveryDetailsVOList, "通过垃圾投递id查询垃圾投递详情信息成功!");
    }
}
