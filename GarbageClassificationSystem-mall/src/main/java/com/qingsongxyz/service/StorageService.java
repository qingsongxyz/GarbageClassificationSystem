package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Storage;
import com.qingsongxyz.vo.StorageVO;

import java.util.List;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
public interface StorageService extends IService<Storage> {

    /**
     * 添加库存对象
     * @param storage 库存对象
     * @return 是否添加成功
     */
    int addStorage(Storage storage);

    /**
     * 删除库存对象
     * @param goodId 商品id
     * @return 是否删除成功
     */
    int deleteStorage(long goodId);

    /**
     * 查询商品库存集合
     * @return 商品库存集合
     */
    List<Storage> getStorageList();

    /**
     * 修改库存
     * @param storage 库存对象
     * @return 是否修改成功
     */
    int updateStorage(Storage storage);

    /**
     * 扣减商品库存难
     * @param goodId 商品id
     * @param number 扣减数
     */
    void decreaseStorage(long goodId, int number);
}

