package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Storage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Mapper
public interface StorageMapper extends BaseMapper<Storage> {

    void decreaseStorage(@Param("goodId") long goodId, @Param("number") int number);
}
