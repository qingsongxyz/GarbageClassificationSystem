package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.pojo.GarbageDeliveryDetails;
import com.qingsongxyz.service.GarbageDeliveryDetailsService;
import com.qingsongxyz.service.GarbageDeliveryService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageDeliveryVO;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private final GarbageDeliveryDetailsService garbageDeliveryDetailsService;

    public GarbageDeliveryController(GarbageDeliveryService garbageDeliveryService, GarbageDeliveryDetailsService garbageDeliveryDetailsService) {
        this.garbageDeliveryService = garbageDeliveryService;
        this.garbageDeliveryDetailsService = garbageDeliveryDetailsService;
    }

    @ApiOperation("测试添加垃圾投递信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGarbageDelivery(@RequestBody @Validated({CreateGroup.class}) GarbageDelivery garbageDelivery) {
        int result = garbageDeliveryService.addGarbageDelivery(garbageDelivery);
        if (result == 0) {
            return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加垃圾投递信息失败!!!");
        }
        return CommonResult.ok("添加垃圾投递信息成功!");
    }

    @ApiOperation("测试通过id删除垃圾投递信息")
    @ApiImplicitParam(name = "id", value = "垃圾投递信息id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGarbageDelivery(@Min(message = "垃圾投递信息id必须大于0", value = 1) @PathVariable("id") long id) {
        long count = garbageDeliveryDetailsService.count(new QueryWrapper<GarbageDeliveryDetails>().eq("delivery_id", id));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在垃圾投递详情信息, 无法删除!!!");
        }
        int result = garbageDeliveryService.deleteGarbageDelivery(id);
        if (result > 0) {
            return CommonResult.ok("通过id删除垃圾投递信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过id删除垃圾投递信息失败!!!");
    }

    @ApiOperation("测试批量删除垃圾投递信息集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteGarbageDeliveryList(@ValidListPositive(message = "垃圾投递信息id都必须大于0") @RequestBody ValidList<Long> ids) {
        boolean flag = true;
        ArrayList<Long> deletedList = new ArrayList<>();
        for (Long id : ids) {
            long count = garbageDeliveryDetailsService.count(new QueryWrapper<GarbageDeliveryDetails>().eq("delivery_id", id));
            if (count > 0) {
                flag = false;
                continue;
            }
            deletedList.add(id);
        }
        int result = garbageDeliveryService.deleteGarbageDeliveryList(deletedList);
        if (!flag) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在垃圾投递详情信息,批量删除垃圾投递信息集合成功!");
        }
        if (result > 0) {
            return CommonResult.ok("批量删除垃圾投递信息集合成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除垃圾投递信息集合失败!!!");
    }

    @ApiOperation("测试分页查询垃圾投递信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1"),
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
    public CommonResult getGarbageDeliveryList(@NotNull(message = "用户id不能为空")
                                               @Min(message = "用户id必须为大于0的数字", value = 1) Long userId,
                                               @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                               @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageDeliveryVO> garbageDeliveryVOList = garbageDeliveryService.getGarbageDeliveryListByUserId(userId, pageNum, pageSize);
        return CommonResult.ok(garbageDeliveryVOList, "分页查询垃圾投递信息成功!");
    }

    @ApiOperation("测试通过投递状态分页查询垃圾投递信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1"),
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
    @GetMapping("/list/{status}/{pageNum}/{pageSize}")
    public CommonResult getGarbageDeliveryListByUserIdAndStatus(@NotNull(message = "用户id不能为空")
                                                                @Min(message = "用户id必须为大于0的数字", value = 1) Long userId,
                                                                @Min(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 0)
                                                                @Max(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 3)
                                                                @PathVariable("status") int status,
                                                                @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                                @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageDeliveryVO> garbageDeliveryVOList = garbageDeliveryService.getGarbageDeliveryListByUserIdAndStatus(userId, status, pageNum, pageSize);
        return CommonResult.ok(garbageDeliveryVOList, "通过投递状态分页查询垃圾投递信息成功!");
    }

    @ApiOperation("测试查询垃圾投递信息总数量")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllGarbageDeliveryCountByUserId(@NotNull(message = "用户id不能为空")
                                                           @Min(message = "用户id必须为大于0的数字", value = 1) long userId) {
        int count = garbageDeliveryService.getAllGarbageDeliveryCountByUserId(userId);
        return CommonResult.ok(count, "查询垃圾投递信息总数量成功!");
    }

    @ApiOperation("测试通过投递状态查询记录总数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "status", value = "投递状态", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/{status}")
    public CommonResult getAllGarbageDeliveryCountByUserIdAndStatus(@NotNull(message = "用户id不能为空")
                                                                    @Min(message = "用户id必须为大于0的数字", value = 1) long userId,
                                                                    @Min(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 0)
                                                                    @Max(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 3)
                                                                    @PathVariable("status") int status) {
        int count = garbageDeliveryService.getAllGarbageDeliveryCountByUserIdAndStatus(userId, status);
        return CommonResult.ok(count, "通过投递状态查询记录总数量成功!");
    }

    @ApiOperation("测试按星期分组查询用户投递次数")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/week")
    public CommonResult getGarbageDeliveryCountGroupByWeek(@NotNull(message = "用户id不能为空")
                                                           @Min(message = "用户id必须为大于0的数字", value = 1) long userId) {
        List<Map<String, Object>> count = garbageDeliveryService.getGarbageDeliveryCountGroupByWeek(userId);
        return CommonResult.ok(count, "按星期分组查询用户投递次数成功!");
    }

    @ApiOperation("测试按投递状态分组查询个人投递次数")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/status")
    public CommonResult getPersonalGarbageDeliveryCountGroupByStatus(@NotNull(message = "用户id不能为空")
                                                                     @Min(message = "用户id必须为大于0的数字", value = 1) long userId) {
        List<Map<String, Object>> count = garbageDeliveryService.getPersonalGarbageDeliveryCountGroupByStatus(userId);
        return CommonResult.ok(count, "按投递状态分组查询个人投递次数成功!");
    }

    @ApiOperation("测试按投递状态分组查询用户投递次数")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/total/status")
    public CommonResult getGarbageDeliveryCountGroupByStatus(){
        List<Map<String, Object>> count = garbageDeliveryService.getGarbageDeliveryCountGroupByStatus();
        return CommonResult.ok(count, "按投递状态分组查询用户投递次数成功!");
    }
}
