package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.GarbageDelivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 垃圾投递表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Mapper
public interface GarbageDeliveryMapper extends BaseMapper<GarbageDelivery> {

    List<GarbageDelivery> getGarbageDeliveryListByUserIdAndStatue(@Param("userId")long userId, @Param("status")Integer status, @Param("start")Integer start, @Param("number")Integer number);

    int getGarbageDeliveryCountByUserIdAndStatus(@Param("userId")long userId, @Param("status")Integer status);
}
