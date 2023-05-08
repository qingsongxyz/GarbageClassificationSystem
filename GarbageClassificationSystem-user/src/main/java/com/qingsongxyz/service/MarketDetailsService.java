package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.MarketDetails;
import com.qingsongxyz.validation.ValidList;

import java.util.List;

/**
 * <p>
 * 购物车详情表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
public interface MarketDetailsService extends IService<MarketDetails> {

    /**
     * 添加购物车详情
     * @param marketDetails 购物车详情对象
     * @return 是否添加成功
     */
    int addMarketDetails(MarketDetails marketDetails);

    /**
     * 删除购物车详情
     * @param id 购物车详情id
     * @return 是否删除成功
     */
    int deleteMarketDetails(long id);

    /**
     * 批量删除购物车详情
     * @param ids 购物车详情信息id集合
     * @return 是否删除成功
     */
    int deleteMarketDetailsList(List<Long> ids);

    /**
     * 批量修改购物车详情信息
     * @param marketDetailsList 购物车详情信息集合
     * @return 是否修改成功
     */
    int updateMarketDetailsList(ValidList<MarketDetails> marketDetailsList);
}
