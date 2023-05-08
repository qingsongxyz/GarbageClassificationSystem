package com.qingsongxyz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.MarketDetailsMapper;
import com.qingsongxyz.pojo.MarketDetails;
import com.qingsongxyz.service.MarketDetailsService;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.MarketVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 购物车详情表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Service
public class MarketDetailsServiceImpl extends ServiceImpl<MarketDetailsMapper, MarketDetails> implements MarketDetailsService {

    private final MarketDetailsMapper marketDetailsMapper;

    public MarketDetailsServiceImpl(MarketDetailsMapper marketDetailsMapper) {
        this.marketDetailsMapper = marketDetailsMapper;
    }

    @Override
    public int addMarketDetails(MarketDetails marketDetails) {
        return marketDetailsMapper.insert(marketDetails);
    }

    @Override
    public int deleteMarketDetails(long id) {
        return marketDetailsMapper.deleteById(id);
    }

    @Override
    public int deleteMarketDetailsList(List<Long> ids) {
        return marketDetailsMapper.deleteBatchIds(ids);
    }

    @Override
    public int updateMarketDetailsList(ValidList<MarketDetails> marketDetailsList) {
        return updateBatchById(marketDetailsList) ? 1 : 0;
    }
}
