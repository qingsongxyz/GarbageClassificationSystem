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
     * 添加垃圾投递详情信息
     * @param garbageDeliveryDetails 垃圾投递详情信息对象
     * @return 是否添加成功
     */
    int addGarbageDeliveryDetails(GarbageDeliveryDetails garbageDeliveryDetails);

    /**
     * 批量添加垃圾投递详情信息
     * @param garbageDeliveryDetailsList 垃圾投递详情信息集合
     * @return 是否批量添加成功
     */
    int addGarbageDeliveryDetailsList(List<GarbageDeliveryDetails> garbageDeliveryDetailsList);

    /**
     * 通过垃圾投递id删除垃圾投递详情信息
     * @param deliveryId 垃圾投递id
     * @return 是否删除成功
     */
    int deleteGarbageDeliveryDetailsByDeliveryId(long deliveryId);

    /**
     * 通过垃圾投递id查询垃圾投递详情信息
     * @param deliveryId 垃圾投递id
     * @return 垃圾投递详情信息
     */
    List<GarbageDeliveryDetailsVO> getGarbageDeliveryDetailsVOByDeliveryId(long deliveryId);

}
