package com.qingsongxyz.service;

import com.qingsongxyz.pojo.GarbageCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.vo.GarbageCategoryVO;
import java.util.List;

/**
 * <p>
 * 垃圾分类表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
public interface GarbageCategoryService extends IService<GarbageCategory> {

    /**
     * 添加垃圾分类
     * @param garbageCategory 垃圾分类
     * @return 是否添加成功
     */
    int addGarbageCategory(GarbageCategory garbageCategory);

    /**
     * 删除垃圾分类信息
     * @param id 垃圾分类的id
     * @return 是否删除成功
     */
    int deleteGarbageCategory(long id);

    /**
     * 批量删除垃圾分类信息
     * @param ids 垃圾分类的id集合
     * @return 是否删除成功
     */
    int deleteGarbageCategoryList(List<Long> ids);

    /**
     * 通过id查询垃圾分类信息
     * @param id 垃圾分类id
     * @return 垃圾分类信息
     */
    GarbageCategoryVO getGarbageCategoryById(long id);

    /**
     * 查询所有垃圾分类
     * @return 垃圾分类集合
     */
    List<GarbageCategoryVO> getAllGarbageCategoryList();

    /**
     * 分页查询所有垃圾分类
     * @return 垃圾分类集合
     */
    List<GarbageCategoryVO> getGarbageCategoryList(int pageNum, int pageSize);

    /**
     * 通过分类名称模糊查询所有垃圾分类信息
     * @param name 分类名称
     * @return 垃圾分类信息
     */
    List<GarbageCategoryVO> getAllGarbageCategoryListByName(String name);

    /**
     * 通过分类名称模糊分页查询垃圾分类信息
     * @param name 分类名称
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾分类信息
     */
    List<GarbageCategoryVO> getGarbageCategoryListByName(String name, int pageNum, int pageSize);

    /**
     * (通过分类名称模糊)查询垃圾分类的总数量
     * @param name 分类名称
     * @return 垃圾分类的总数量
     */
    long getGarbageCategoryCount(String name);

    /**
     * 修改垃圾分类信息
     * @param garbageCategory 垃圾分类信息
     * @return 是否修改成功
     */
    int updateGarbageCategory(GarbageCategory garbageCategory);

}
