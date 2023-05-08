package com.qingsongxyz.service;

import com.qingsongxyz.pojo.Garbage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.vo.GarbageVO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 垃圾信息表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
public interface GarbageService extends IService<Garbage> {

    /**
     * 添加垃圾信息
     * @param garbage 垃圾对象
     * @return 是否添加成功
     */
    int addGarbage(Garbage garbage);

    /**
     * 删除垃圾信息
     * @param id 垃圾的id
     * @return 是否删除成功
     */
    int deleteGarbage(long id);

    /**
     * 批量删除垃圾信息
     * @param ids 垃圾id集合
     * @return 是否删除成功
     */
    int deleteGarbageList(List<Long> ids);

    /**
     * 通过id查询垃圾信息
     * @param id 垃圾的id
     * @return 垃圾信息
     */
    GarbageVO getGarbageById(long id);

    /**
     * 通过keyword查询所有垃圾信息
     * @param keyword 关键词
     * @param isHighlight 是否需要高亮
     * @return 垃圾集合
     */
    List<GarbageVO> getAllGarbageByKeyword(String keyword, String isHighlight) throws IOException;

    /**
     * 通过keyword分页查询垃圾信息
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾集合
     */
    List<GarbageVO> getGarbageByKeyword(String keyword, int pageNum, int pageSize) throws IOException;

    /**
     * 通过keyword查询垃圾总数量
     * @param keyword 关键词
     * @return 垃圾总数量
     */
    long getGarbageCountByKeyword(String keyword) throws IOException;

    /**
     * 查询所有垃圾信息
     * @return 垃圾集合
     */
    List<GarbageVO> getAllGarbageList();

    /**
     * 查询所有垃圾分页返回
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾集合
     */
    List<GarbageVO> getGarbageList(int pageNum, int pageSize);

    /**
     * 通过垃圾名称模糊查询所有垃圾
     * @param garbageName 垃圾名称
     * @return 垃圾集合
     */
    List<GarbageVO> geAllGarbageListByName(String garbageName);

    /**
     * (通过垃圾名称模糊、分类名称)查询垃圾分页返回
     * @param garbageName 垃圾名称
     * @param categoryName 分类名称
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 垃圾集合
     */
    List<GarbageVO> getGarbageListByNameOrCategory(String garbageName, String categoryName, int pageNum, int pageSize);

    /**
     * (通过垃圾名称模糊、垃圾分类)查询垃圾总数量
     * @param garbageName 垃圾名称
     * @param categoryName 垃圾分类名称
     * @return 垃圾总数量
     */
    long getGarbageCount(String garbageName, String categoryName);

    /**
     * 按垃圾种类分组查询垃圾数量
     * @return 垃圾数量
     */
    List<Map<String, Object>> getGarbageCountGroupByCategory();

    /**
     * 修改垃圾信息
     * @param garbage 垃圾信息
     * @return 是否修改成功
     */
    int updateGarbage(Garbage garbage);

}
