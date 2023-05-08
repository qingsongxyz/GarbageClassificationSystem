package com.qingsongxyz.controller;

import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.OrderDetailsService;
import com.qingsongxyz.vo.OrderDetailsVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>
 * 订单详情表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Api(tags = "订单详情类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/order-details")
public class OrderDetailsController {

    private final OrderDetailsService orderDetailsService;

    public OrderDetailsController(OrderDetailsService orderDetailsService) {
        this.orderDetailsService = orderDetailsService;
    }

    @ApiOperation("测试查询订单的详情信息")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/{orderId}")
    public CommonResult getOrderDetailsList(@Min(message = "订单id必须为大于0的数字", value = 1) @PathVariable("orderId") long orderId) {
        List<OrderDetailsVO> orderDetailsVOList = orderDetailsService.getOrderDetailsList(orderId);
        return CommonResult.ok(orderDetailsVOList, "查询订单的详情信息成功!");
    }
}
