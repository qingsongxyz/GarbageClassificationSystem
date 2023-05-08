package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.GarbageDeliveryDetails;
import com.qingsongxyz.vo.GarbageDeliveryDetailsVO;

import java.util.List;

/**
 * <p>
 * 垃圾投递详情表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
public interface GarbageDeliveryDetailsService extends IService<GarbageDeliveryDetails> {

    /**
     * 通过垃圾投递id查询垃圾投递详情信息
     * @param deliveryId 垃圾投递id
     * @return 垃圾投递详情信息
     */
    List<GarbageDeliveryDetailsVO> getGarbageDeliveryDetailsListByDeliveryId(long deliveryId);
}
