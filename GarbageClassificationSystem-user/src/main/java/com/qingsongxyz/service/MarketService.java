package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Market;
import com.qingsongxyz.vo.MarketVO;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
public interface MarketService extends IService<Market> {

    /**
     * 添加购物车
     *
     * @param market 购物车对象
     * @return 是否添加成功
     */
    int addMarket(Market market);

    /**
     * 删除购物车
     *
     * @param id 购物车id
     * @return 是否删除成功
     */
    int deleteMarket(long id);

    /**
     * 通过用户id查询购物车信息
     *
     * @param userId 用户id
     * @return 购物车信息
     */
    MarketVO getMarketByUserId(long userId);

    /**
     * 通过用户id查询详情信息的数量
     * @param userId 用户id
     * @return 购物车详情信息的数量
     */
    int getMarketDetailsCountByUserId(long userId);

}
