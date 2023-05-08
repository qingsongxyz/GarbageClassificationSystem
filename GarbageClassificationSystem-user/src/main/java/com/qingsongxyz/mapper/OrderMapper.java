package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
