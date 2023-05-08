package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.excel.GoodTemplate;
import com.qingsongxyz.pojo.Good;
import com.qingsongxyz.vo.GoodVO;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
public interface GoodService extends IService<Good> {

    /**
     * 添加商品
     * @param good 商品对象
     * @return 是否添加成功
     */
    int addGood(Good good);

    /**
     * 删除商品
     * @param id 商品id
     * @return 是否删除成功
     */
    int deleteGood(long id);

    /**
     * 批量删除商品
     * @param ids 商品id集合
     * @return 是否删除成功
     */
    int deleteGoodList(List<Long> ids);

    /**
     * 查询商品集合
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 商品集合
     */
    List<GoodVO> getGoodList(int pageNum, int pageSize);

    /**
     * 通过种类名称、商品名称模糊查询商品集合
     * @param name 商品名称
     * @param category 商品种类名称
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 商品集合
     */
    List<GoodVO> getGoodListByNameOrCategory(String name, String category, int pageNum, int pageSize);

    /**
     * 通过商品名称模糊查询所有商品集合
     * @param name 商品名称
     * @return 所有商品集合
     */
    List<GoodVO> getAllGoodListByName(String name);

    /**
     * 查询所有商品集合
     * @return 所有商品集合
     */
    List<GoodTemplate> getAllGoodList();

    /**
     * 查询商品总数量
     * @return 商品总数量
     */
    int getAllGoodCount();

    /**
     * 通过种类名称、商品名称模糊查询商品数量
     * @param name 商品名称
     * @param category 商品种类名称
     * @return 商品数量
     */
    int getGoodByNameOrCategory(String name, String category);

    /**
     * 修改商品
     * @param good 商品对象
     * @return 是否修改成功
     */
    int updateGood(Good good);

}
