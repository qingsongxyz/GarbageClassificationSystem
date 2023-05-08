package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.GoodCategory;
import com.qingsongxyz.vo.GoodCategoryVO;

import java.util.List;

/**
 * <p>
 * 商品种类表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
public interface GoodCategoryService extends IService<GoodCategory> {

    /**
     * 添加商品种类
     * @param goodCategory 商品种类
     * @return 是否添加成功
     */
    int addGoodCategory(GoodCategory goodCategory);

    /**
     * 删除商品种类
     * @param id 商品id
     * @return 是否删除成功
     */
    int deleteGoodCategory(long id);

    /**
     * 批量删除商品种类
     * @param ids 商品id集合
     * @return 是否删除成功
     */
    int deleteGoodCategoryList(List<Long> ids);

    /**
     * 查询商品种类集合
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 商品种类集合
     */
    List<GoodCategoryVO> getGoodCategoryList(int pageNum, int pageSize);

    /**
     * 通过种类名称模糊查询商品种类集合
     * @param category 种类名称
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 商品种类集合
     */
    List<GoodCategoryVO> getGoodCategoryListByCategory(String category, int pageNum, int pageSize);

    /**
     * 通过种类名称模糊查询所有商品种类集合
     * @param category 种类名称
     * @return 商品种类集合
     */
    List<GoodCategoryVO> getAllGoodCategoryListByCategory(String category);

    /**
     * 查询所有商品种类集合
     * @return 商品种类集合
     */
    List<GoodCategoryVO> getAllGoodCategoryList();

    /**
     * 查询商品种类总数
     * @return 商品种类总数
     */
    int getAllGoodCategoryCount();

    /**
     * 通过种类名称模糊查询商品种类数量
     * @param category 种类名称
     * @return 商品种类数量
     */
    int getGoodCategoryCountByCategory(String category);

    /**
     * 修改商品种类
     * @param goodCategory 商品种类
     * @return 是否修改成功
     */
    int updateGoodCategory(GoodCategory goodCategory);

}
