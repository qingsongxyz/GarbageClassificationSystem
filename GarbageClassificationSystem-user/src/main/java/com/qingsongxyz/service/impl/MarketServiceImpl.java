package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.MarketMapper;
import com.qingsongxyz.pojo.Market;
import com.qingsongxyz.service.MarketService;
import com.qingsongxyz.vo.MarketVO;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService {

    private final MarketMapper marketMapper;

    public MarketServiceImpl(MarketMapper marketMapper) {
        this.marketMapper = marketMapper;
    }

    @Override
    public int addMarket(Market market) {
        return marketMapper.insert(market);
    }

    @Override
    public int deleteMarket(long id) {
        return marketMapper.deleteById(id);
    }

    @Override
    public MarketVO getMarketByUserId(long userId) {
        Market market = marketMapper.getMarketByUserId(userId);
        MarketVO marketVO = BeanUtil.copyProperties(market, MarketVO.class);
        return marketVO;
    }

    @Override
    public int getMarketDetailsCountByUserId(long userId) {
        return marketMapper.getMarketDetailsCountByUserId(userId);
    }
}
