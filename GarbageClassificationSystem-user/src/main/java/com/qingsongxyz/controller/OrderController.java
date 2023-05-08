package com.qingsongxyz.controller;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Order;
import com.qingsongxyz.service.OrderService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.vo.OrderVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Api(tags = "订单类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ApiOperation("测试支付订单")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/pay")
    public CommonResult pay(@RequestBody @Validated({CreateGroup.class}) Order order) throws Exception {
        orderService.pay(order);
        return CommonResult.ok("支付订单成功!");
    }

    @ApiOperation("测试分页查询用户订单列表")
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
    public CommonResult getOrderList(@NotNull(message = "用户id不能为空")
                                     @Min(message = "用户id必须为大于0的数字", value = 1) long userId,
                                     @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                     @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize){
        List<OrderVO> orderVOList = orderService.getOrderList(userId, pageNum, pageSize);
        return CommonResult.ok(orderVOList, "分页查询用户订单列表成功!");
    }

    @ApiOperation("测试查询用户订单总数量")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllOrderCount(@NotNull(message = "用户id不能为空")
                                         @Min(message = "用户id必须为大于0的数字", value = 1) long userId){
        int count = orderService.getAllOrderCount(userId);
        return CommonResult.ok(count, "查询用户订单总数量成功!");
    }
}
