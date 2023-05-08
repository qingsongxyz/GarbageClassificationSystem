package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.OrderDetailsMapper;
import com.qingsongxyz.pojo.OrderDetails;
import com.qingsongxyz.service.OrderDetailsService;
import com.qingsongxyz.vo.OrderDetailsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Service
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements OrderDetailsService {

    private final OrderDetailsMapper orderDetailsMapper;

    public OrderDetailsServiceImpl(OrderDetailsMapper orderDetailsMapper) {
        this.orderDetailsMapper = orderDetailsMapper;
    }

    @Override
    public int addOrderDetailsList(List<OrderDetails> orderDetailsList) {
        return saveBatch(orderDetailsList) ? 1 : 0;
    }

    @Override
    public List<OrderDetailsVO> getOrderDetailsList(long orderId) {
        List<OrderDetailsVO> orderDetailsVOList = new ArrayList<>();
        List<OrderDetails> orderDetailsList = list(new QueryWrapper<OrderDetails>().eq("order_id", orderId).eq("deleted", 0));
        orderDetailsList.forEach(od -> {
            OrderDetailsVO orderDetailsVO = BeanUtil.copyProperties(od, OrderDetailsVO.class);
            orderDetailsVOList.add(orderDetailsVO);
        });
        return orderDetailsVOList;
    }
}
