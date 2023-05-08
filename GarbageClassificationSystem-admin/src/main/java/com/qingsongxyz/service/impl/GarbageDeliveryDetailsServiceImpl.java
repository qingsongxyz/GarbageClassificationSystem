package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.GarbageDeliveryDetailsMapper;
import com.qingsongxyz.pojo.GarbageDeliveryDetails;
import com.qingsongxyz.service.GarbageDeliveryDetailsService;
import com.qingsongxyz.vo.GarbageDeliveryDetailsVO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 垃圾投递详情表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Service
public class GarbageDeliveryDetailsServiceImpl extends ServiceImpl<GarbageDeliveryDetailsMapper, GarbageDeliveryDetails> implements GarbageDeliveryDetailsService {

    private final GarbageDeliveryDetailsMapper garbageDeliveryDetailsMapper;

    public GarbageDeliveryDetailsServiceImpl(GarbageDeliveryDetailsMapper garbageDeliveryDetailsMapper) {
        this.garbageDeliveryDetailsMapper = garbageDeliveryDetailsMapper;
    }

    @Override
    public List<GarbageDeliveryDetailsVO> getGarbageDeliveryDetailsListByDeliveryId(long deliveryId) {
        List<GarbageDeliveryDetails> garbageDeliveryDetailsList = list(new QueryWrapper<GarbageDeliveryDetails>().eq("delivery_id", deliveryId));
        return transfer(garbageDeliveryDetailsList);
    }

    /**
     * 将垃圾投递详情信息集合转换为垃圾投递详情信息VO集合
     * @param garbageDeliveryDetailsList 垃圾投递详情信息集合
     * @return 垃圾投递详情信息VO集合
     */
    private List<GarbageDeliveryDetailsVO> transfer(List<GarbageDeliveryDetails> garbageDeliveryDetailsList){
        ArrayList<GarbageDeliveryDetailsVO> garbageDeliveryDetailsVOList = new ArrayList<>(garbageDeliveryDetailsList.size());
        garbageDeliveryDetailsList.forEach(d -> {
            GarbageDeliveryDetailsVO garbageDeliveryDetailsVO = BeanUtil.copyProperties(d, GarbageDeliveryDetailsVO.class);
            garbageDeliveryDetailsVOList.add(garbageDeliveryDetailsVO);
        });
        return garbageDeliveryDetailsVOList;
    }
}
