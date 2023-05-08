package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Order;
import com.qingsongxyz.vo.OrderVO;
import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
public interface OrderService extends IService<Order> {

    /**
     * 添加订单
     * @param order 订单对象
     * @return 是否添加成功
     */
    int addOrder(Order order);

    /**
     * 支付订单
     * @param order 订单对象
     */
    void pay(Order order) throws Exception;

    /**
     * 分页查询用户订单列表
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 用户订单列表
     */
    List<OrderVO> getOrderList(long userId, int pageNum, int pageSize);

    /**
     * 查询用户订单总数量
     * @param userId 用户id
     * @return 用户订单总数量
     */
    int getAllOrderCount(long userId);
}

