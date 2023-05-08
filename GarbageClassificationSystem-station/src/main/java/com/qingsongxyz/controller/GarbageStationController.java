package com.qingsongxyz.controller;


import cn.hutool.http.HttpStatus;
import com.qingsongxyz.constraints.Coordinate;
import com.qingsongxyz.constraints.DoubleMin;
import com.qingsongxyz.pojo.Address;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.GarbageStation;
import com.qingsongxyz.service.GarbageStationService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 垃圾回收站前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-2-08
 */
@Api(tags = "垃圾回收站测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/garbage-station")
public class GarbageStationController {

    private final GarbageStationService garbageStationService;

    public GarbageStationController(GarbageStationService garbageStationService) {
        this.garbageStationService = garbageStationService;
    }

    @ApiOperation("测试添加垃圾回收站")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGarbageStation(@RequestBody @Validated({CreateGroup.class}) GarbageStation garbageStation) {
        int result = garbageStationService.addGarbageStation(garbageStation);
        if (result > 0) {
            return CommonResult.ok(garbageStation, "添加垃圾回收站成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加垃圾回收站失败!!!");
    }

    @ApiOperation("测试删除垃圾回收站")
    @ApiImplicitParam(name = "id", value = "垃圾回收站id", required = true, paramType = "path", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGarbageStation(@NotBlank(message = "垃圾回收站id不能为空")
                                             @PathVariable("id") String id) {
        int result = garbageStationService.deleteGarbageStation(id);
        if (result > 0) {
            return CommonResult.ok("删除垃圾回收站成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除垃圾回收站失败!!!");
    }

    @ApiOperation("测试通过id查询垃圾回收站")
    @ApiImplicitParam(name = "id", value = "垃圾回收站id", required = true, paramType = "path", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getGarbageStationById(@NotBlank(message = "垃圾回收站id不能为空")
                                              @PathVariable("id") String id) {
        GarbageStation garbageStation = garbageStationService.getGarbageStationById(id);
        return CommonResult.ok(garbageStation, "通过id查询垃圾回收站成功!");
    }

    @ApiOperation("测试通过地址查询垃圾回收站集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/list/address")
    public CommonResult getGarbageStationListByAddress(@NotNull(message = "垃圾站地址不能为空")
                                                       @RequestBody Address address) {
        List<GarbageStation> garbageStationList = garbageStationService.getGarbageStationListByAddress(address);
        return CommonResult.ok(garbageStationList, "通过地址查询垃圾回收站集合成功!");
    }

    @ApiOperation("测试查询在中心点范围内的所有垃圾回收站集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coordinates", value = "中心点经纬度", required = true, paramType = "query", dataTypeClass = double[].class, example = "[1,1]"),
            @ApiImplicitParam(name = "distance", value = "离中心点距离", required = true, paramType = "query", dataTypeClass = Double.class, example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/near")
    public CommonResult getNearGarbageStationList(@NotNull(message = "中心点经纬度不能为空")
                                                  @Size(message = "中心点坐标必须为经纬度", min = 2, max = 2)
                                                  @Coordinate(message = "中心点经纬度必须合法") double[] coordinates,
                                                  @NotNull(message = "离中心点距离(千米)不能为空")
                                                  @DoubleMin(value = 0, message = "离中心点距离(千米)必须大于0") Double distance) {
        List<GarbageStation> garbageStationList = garbageStationService.getNearGarbageStationList(coordinates, distance);
        return CommonResult.ok(garbageStationList, "查询在中心点范围内的所有垃圾回收站集合成功!");
    }

    @ApiOperation("测试修改垃圾回收站信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateGarbageStation(@RequestBody @Validated({UpdateGroup.class}) GarbageStation garbageStation) {
        int result = garbageStationService.updateGarbageStation(garbageStation);
        if (result > 0) {
            return CommonResult.ok("修改垃圾回收站信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改垃圾回收站信息失败!!!");
    }
}
