package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Market;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
public interface MarketService extends IService<Market> {

    /**
     * 添加购物车
     * @param userId 用户id
     * @return 是否添加成功
     */
    int addMarket(long userId);
}
