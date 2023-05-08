package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Market;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Mapper
public interface MarketMapper extends BaseMapper<Market> {

    Market getMarketByUserId(@Param("userId") Long userId);

    int getMarketDetailsCountByUserId(@Param("userId") Long userId);
}
