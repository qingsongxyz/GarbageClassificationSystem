package com.qingsongxyz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.MarketMapper;
import com.qingsongxyz.pojo.Market;
import com.qingsongxyz.service.MarketService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService {

    private final MarketMapper marketMapper;

    public MarketServiceImpl(MarketMapper marketMapper) {
        this.marketMapper = marketMapper;
    }

    @Override
    public int addMarket(long userId) {
        Market market = new Market();
        market.setUserId(userId);
        return marketMapper.insert(market);
    }
}
