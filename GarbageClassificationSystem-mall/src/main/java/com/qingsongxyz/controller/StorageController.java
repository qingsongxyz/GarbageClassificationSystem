package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Storage;
import com.qingsongxyz.service.StorageService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 库存表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Api(tags = "商品库存类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiOperation("测试添加库存对象")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addStorage(@RequestBody @Validated({CreateGroup.class}) Storage storage) {
        int result = storageService.addStorage(storage);
        if (result > 0) {
            return CommonResult.ok("添加库存对象成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加库存对象失败!!!");
    }

    @ApiOperation("测试删除库存")
    @ApiImplicitParam(name = "id", value = "库存id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteStorage(@Min(message = "库存id必须为大于0的数字", value = 1)
                                      @PathVariable("id") long id) {
        int result = storageService.deleteStorage(id);
        if (result > 0) {
            return CommonResult.ok("删除库存成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除库存失败!!!");
    }

    @ApiOperation("测试修改库存")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateStorage(@RequestBody @Validated({UpdateGroup.class}) Storage storage) {
        int result = storageService.updateStorage(storage);
        if (result > 0) {
            return CommonResult.ok("修改库存成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改库存失败!!!");
    }

    @ApiOperation("测试扣减商品库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodId", value = "商品id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "number", value = "扣减数量", required = true, paramType = "path", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/decrease/{goodId}")
    public CommonResult decreaseStorage(@Min(message = "商品id必须为大于0的数字", value = 1)
                                        @PathVariable("goodId") long goodId,
                                        @NotNull(message = "扣减数量不能为空")
                                        @Min(message = "扣减数量必须大于0", value = 1) int number) {
        storageService.decreaseStorage(goodId, number);
        return CommonResult.ok("扣减商品库存成功!");
    }
}
