package com.qingsongxyz.service;

import com.qingsongxyz.pojo.Address;
import com.qingsongxyz.pojo.GarbageStation;
import java.util.List;

/**
 * <p>
 * 垃圾回收站 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-2-08
 */
public interface GarbageStationService {

    /**
     * 添加垃圾回收站
     * @param garbageStation 垃圾回收站对象
     * @return 是否添加成功
     */
    int addGarbageStation(GarbageStation garbageStation);

    /**
     * 删除垃圾回收站
     * @param id 垃圾回收站id
     * @return 是否删除成功
     */
    int deleteGarbageStation(String id);

    /**
     * 通过id查询垃圾回收站
     * @param id 垃圾回收站id
     * @return 垃圾回收站
     */
    GarbageStation getGarbageStationById(String id);

    /**
     * 通过地址查询垃圾回收站集合
     * @param address 地址对象
     * @return 垃圾回收站集合
     */
    List<GarbageStation> getGarbageStationListByAddress(Address address);

    /**
     * 查询在中心点范围内的所有垃圾回收站集合
     * @param coordinates 中心点经纬度
     * @param distance 最大距离
     * @return 垃圾回收站集合
     */
    List<GarbageStation> getNearGarbageStationList(double[] coordinates, double distance);

    /**
     * 修改垃圾回收站信息
     * @param garbageStation 垃圾回收站对象
     * @return 是否修改成功
     */
    int updateGarbageStation(GarbageStation garbageStation);
}
