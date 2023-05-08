package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Mapper
public interface GoodMapper extends BaseMapper<Good> {

    List<Good> getGoodList(@Param("name") String name, @Param("category") String category, @Param("start") Integer start, @Param("number") Integer number);

    int getGoodCount(@Param("name") String name, @Param("category") String category);
}
