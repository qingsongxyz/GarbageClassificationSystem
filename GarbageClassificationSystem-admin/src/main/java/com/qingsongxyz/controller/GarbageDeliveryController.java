package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.service.GarbageDeliveryService;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageDeliveryVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * <p>
 * 垃圾投递表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Api(tags = "垃圾投递类测试")
@Validated //非pojo参数检验声明
@RestController
@RequestMapping("/garbage-delivery")
public class GarbageDeliveryController {

    private final GarbageDeliveryService garbageDeliveryService;

    public GarbageDeliveryController(GarbageDeliveryService garbageDeliveryService) {
        this.garbageDeliveryService = garbageDeliveryService;
    }

    @ApiOperation("测试分页查询垃圾投递信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页面容量", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/{pageNum}/{pageSize}")
    public CommonResult getGarbageDeliveryList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                               @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageDeliveryVO> garbageDeliveryVOList = garbageDeliveryService.getGarbageDeliveryList(pageNum, pageSize);
        return CommonResult.ok(garbageDeliveryVOList, "分页查询垃圾投递信息成功!");
    }

    @ApiOperation("测试通过投递状态和用户名分页模糊查询垃圾投递信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataTypeClass = String.class, example = "zhangsan"),
            @ApiImplicitParam(name = "status", value = "投递状态", required = true, paramType = "query", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页面容量", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/username/status/{pageNum}/{pageSize}")
    public CommonResult getGarbageDeliveryListByUsernameOrStatus(@NotBlank(message = "用户名不能为空")
                                                                 @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20)
                                                                 @Pattern(regexp = "^\\w+$", message = "用户名只能包含数字、英文字符和下划线") String username,
                                                                 @Min(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 0)
                                                                 @Max(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 3) int status,
                                                                 @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                                 @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageDeliveryVO> garbageDeliveryVOList = garbageDeliveryService.getGarbageDeliveryListByUsernameOrStatus(username, status, pageNum, pageSize);
        return CommonResult.ok(garbageDeliveryVOList, "通过投递状态和用户名分页模糊查询垃圾投递信息成功!");
    }

    @ApiOperation("测试查询垃圾投递信息总数量")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllGarbageDeliveryCount() {
        int count = garbageDeliveryService.getAllGarbageDeliveryCount();
        return CommonResult.ok(count, "查询垃圾投递信息总数量成功!");
    }

    @ApiOperation("测试通过投递状态和用户名模糊查询记录总数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataTypeClass = String.class, example = "zhangsan"),
            @ApiImplicitParam(name = "status", value = "投递状态", required = true, paramType = "query", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/username/status")
    public CommonResult getAllGarbageDeliveryCountByUsernameOrStatus(@NotBlank(message = "用户名不能为空")
                                                                     @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20)
                                                                     @Pattern(regexp = "^\\w+$", message = "用户名只能包含数字、英文字符和下划线") String username,
                                                                     @Min(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 0)
                                                                     @Max(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 3) int status) {
        int count = garbageDeliveryService.getAllGarbageDeliveryCountByUsernameOrStatus(username, status);
        return CommonResult.ok(count, "通过投递状态和用户名模糊查询记录总数量成功!");
    }

    @ApiOperation("测试修改投递记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/list")
    public CommonResult updateGarbageDelivery(@RequestBody @Validated({UpdateGroup.class}) ValidList<GarbageDelivery> garbageDeliveryList) {
        int result = garbageDeliveryService.updateGarbageDelivery(garbageDeliveryList);
        if (result > 0) {
            return CommonResult.ok("修改投递记录成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改投递记录失败!!!");
    }
}
