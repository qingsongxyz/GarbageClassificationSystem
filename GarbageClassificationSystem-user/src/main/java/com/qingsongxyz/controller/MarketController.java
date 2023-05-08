package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Market;
import com.qingsongxyz.service.MarketService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.vo.MarketVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Api(tags = "购物车类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @ApiOperation("测试添加购物车")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addMarket(@RequestBody @Validated({CreateGroup.class}) Market market) {
        int result = marketService.addMarket(market);
        if (result > 0) {
            return CommonResult.ok("添加购物车成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加购物车失败!!!");
    }

    @ApiOperation("测试删除购物车")
    @ApiImplicitParam(name = "id", value = "购物车id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteMarket(@Min(message = "购物车id必须为大于0的数字", value = 1) @PathVariable("id") long id) {
        int result = marketService.deleteMarket(id);
        if (result > 0) {
            return CommonResult.ok("删除购物车成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除购物车失败!!!");
    }

    @ApiOperation("测试通过用户id查询购物车信息")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{userId}")
    public CommonResult getMarketByUserId(@Min(message = "用户id必须为大于0的数字", value = 1) @PathVariable("userId") long userId){
        MarketVO marketVO = marketService.getMarketByUserId(userId);
        return CommonResult.ok(marketVO, "通过用户id查询购物车信息成功!");
    }

    @ApiOperation("测试通过用户id查询详情信息的数量")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/{userId}")
    public CommonResult getMarketDetailsCountByUserId(@Min(message = "用户id必须为大于0的数字", value = 1) @PathVariable("userId") long userId){
        int count = marketService.getMarketDetailsCountByUserId(userId);
        return CommonResult.ok(count, "通过用户id查询详情信息的数量成功!");
    }
}
