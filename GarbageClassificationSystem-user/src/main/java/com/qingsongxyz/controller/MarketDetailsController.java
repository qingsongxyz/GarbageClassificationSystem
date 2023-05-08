package com.qingsongxyz.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.MarketDetails;
import com.qingsongxyz.service.MarketDetailsService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>
 * 购物车详情表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Api(tags = "购物车详情类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/market-details")
public class MarketDetailsController {

    private final MarketDetailsService marketDetailsService;

    public MarketDetailsController(MarketDetailsService marketDetailsService) {
        this.marketDetailsService = marketDetailsService;
    }

    @ApiOperation("测试添加购物车详情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addMarketDetails(@RequestBody @Validated({CreateGroup.class}) MarketDetails marketDetails) {
        int result = 0;
        //如果购物车存在同样的商品 只需修改数量即可
        MarketDetails one = marketDetailsService.getOne(new QueryWrapper<MarketDetails>()
                .eq("market_id", marketDetails.getMarketId())
                .eq("good_id", marketDetails.getGoodId())
                .eq("deleted", 0));
        if (ObjectUtil.isNotNull(one)) {
            one.setCount(one.getCount() + marketDetails.getCount());
            result = marketDetailsService.updateById(one) ? 1 : 0;
        } else {
            result = marketDetailsService.addMarketDetails(marketDetails);
        }
        if (result > 0) {
            return CommonResult.ok("添加购物车详情成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加购物车详情失败!!!");
    }

    @ApiOperation("测试删除购物车详情")
    @ApiImplicitParam(name = "id", value = "购物车详情id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteMarketDetails(@Min(message = "购物车详情id必须为大于0的数字", value = 1) @PathVariable("id") long id) {
        int result = marketDetailsService.deleteMarketDetails(id);
        if (result > 0) {
            return CommonResult.ok("删除购物车详情成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除购物车详情失败!!!");
    }

    @ApiOperation("测试批量删除购物车详情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteMarketDetailsList(@ValidListPositive(message = "购物车详情id都必须为大于0") @RequestBody ValidList<Long> ids) {
        int result = marketDetailsService.deleteMarketDetailsList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除购物车详情成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除购物车详情失败!!!");
    }

    @ApiOperation("测试批量修改购物车详情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/list")
    public CommonResult updateMarketDetailsList(@RequestBody @Validated({UpdateGroup.class}) ValidList<MarketDetails> marketDetailsList) {
        int result = marketDetailsService.updateMarketDetailsList(marketDetailsList);
        if (result > 0) {
            return CommonResult.ok("批量修改购物车详情成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量修改购物车详情失败!!!");
    }
}
