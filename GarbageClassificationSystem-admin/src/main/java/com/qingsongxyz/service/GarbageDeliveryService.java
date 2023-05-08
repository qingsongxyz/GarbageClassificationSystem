package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageDeliveryVO;

import java.util.List;

/**
 * <p>
 * 垃圾投递表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
public interface GarbageDeliveryService extends IService<GarbageDelivery> {



    /**
     * 分页查询垃圾投递信息
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾投递信息
     */
    List<GarbageDeliveryVO> getGarbageDeliveryList(int pageNum, int pageSize);

    /**
     * 通过投递状态和用户名分页模糊查询垃圾投递信息
     * @param username 用户名
     * @param status 投递状态
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾投递信息VO集合
     */
    List<GarbageDeliveryVO> getGarbageDeliveryListByUsernameOrStatus(String username, int status, int pageNum, int pageSize);

    /**
     * 查询垃圾投递总数量
     * @return 垃圾投递总数量
     */
    int getAllGarbageDeliveryCount();

    /**
     * 通过投递状态和用户名模糊查询记录总数量
     * @param username 用户名
     * @param status 投递状态
     * @return 记录总数量
     */
    int getAllGarbageDeliveryCountByUsernameOrStatus(String username, int status);

    /**
     * 批量修改投递记录
     * @param garbageDeliveryList 投递对象集合
     * @return 是否修改成功
     */
    int updateGarbageDelivery(ValidList<GarbageDelivery> garbageDeliveryList);
}
