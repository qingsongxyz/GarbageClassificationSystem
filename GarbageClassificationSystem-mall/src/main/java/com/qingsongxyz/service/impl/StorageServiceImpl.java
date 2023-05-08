package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.StorageMapper;
import com.qingsongxyz.pojo.Storage;
import com.qingsongxyz.service.StorageService;
import com.qingsongxyz.vo.StorageVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Service
class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

    private final StorageMapper storageMapper;

    public StorageServiceImpl(StorageMapper storageMapper) {
        this.storageMapper = storageMapper;
    }

    @Override
    public int addStorage(Storage storage) {
        return storageMapper.insert(storage);
    }

    @Override
    public int deleteStorage(long goodId) {
        return storageMapper.delete(new QueryWrapper<Storage>().eq("good_id", goodId));
    }

    @Override
    public List<Storage> getStorageList() {
        return list();
    }

    @Override
    public int updateStorage(Storage storage) {
        return storageMapper.updateById(storage);
    }

    @Override
    public void decreaseStorage(long goodId, int number) {
        storageMapper.decreaseStorage(goodId, number);
    }
}
