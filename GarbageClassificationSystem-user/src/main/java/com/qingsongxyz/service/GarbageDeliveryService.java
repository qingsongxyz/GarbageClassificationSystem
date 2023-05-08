package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.vo.GarbageDeliveryVO;

import java.util.List;
import java.util.Map;

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
     * 添加垃圾投递信息
     * @param garbageDelivery 垃圾投递对象
     * @return 是否添加成功
     */
    int addGarbageDelivery(GarbageDelivery garbageDelivery);

    /**
     * 通过id删除垃圾投递信息
     * @param id 垃圾投递id
     * @return 是否删除成功
     */
    int deleteGarbageDelivery(long id);

    /**
     * 批量删除垃圾投递信息集合
     * @param ids 垃圾投递信息id集合
     * @return 是否批量删除成功
     */
    int deleteGarbageDeliveryList(List<Long> ids);

    /**
     * 分页查询垃圾投递信息
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾投递信息VO集合
     */
    List<GarbageDeliveryVO> getGarbageDeliveryListByUserId(long userId, int pageNum, int pageSize);

    /**
     * 通过投递状态分页查询垃圾投递信息
     * @param userId 用户id
     * @param status 投递状态
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾投递信息VO集合
     */
    List<GarbageDeliveryVO> getGarbageDeliveryListByUserIdAndStatus(long userId, int status, int pageNum, int pageSize);

    /**
     * 查询垃圾投递总数量
     * @param userId 用户id
     * @return 垃圾投递总数量
     */
    int getAllGarbageDeliveryCountByUserId(long userId);

    /**
     * 通过投递状态查询记录总数量
     * @param userId 用户id
     * @param status 投递状态
     * @return 记录总数量
     */
    int getAllGarbageDeliveryCountByUserIdAndStatus(long userId, int status);

    /**
     * 查询垃圾投递信息总数量
     * @return 垃圾投递信息总数量
     */
    int getGarbageDeliveryCount();

    /**
     * 按星期分组查询用户投递次数
     * @param userId 用户id
     * @return 用户投递次数
     */
    List<Map<String, Object>> getGarbageDeliveryCountGroupByWeek(long userId);

    /**
     * 按投递状态分组查询个人投递次数
     * @param userId 用户id
     * @return 个人投递次数
     */
    List<Map<String, Object>> getPersonalGarbageDeliveryCountGroupByStatus(long userId);

    /**
     * 按投递状态分组查询用户投递次数
     * @return 用户投递次数
     */
    List<Map<String, Object>> getGarbageDeliveryCountGroupByStatus();

}
