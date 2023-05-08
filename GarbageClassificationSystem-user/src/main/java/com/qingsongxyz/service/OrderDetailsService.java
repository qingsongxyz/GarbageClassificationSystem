package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.OrderDetails;
import com.qingsongxyz.vo.OrderDetailsVO;

import java.util.List;

/**
 * <p>
 * 订单详情表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
public interface OrderDetailsService extends IService<OrderDetails> {

    /**
     * 批量添加订单详情信息
     * @param orderDetailsList 订单详情信息对象集合
     * @return 是否添加成功
     */
    int addOrderDetailsList(List<OrderDetails> orderDetailsList);

    /**
     * 查询订单的详情信息
     * @param orderId 订单id
     * @return 订单的详情信息
     */
    List<OrderDetailsVO> getOrderDetailsList(long orderId);
}
