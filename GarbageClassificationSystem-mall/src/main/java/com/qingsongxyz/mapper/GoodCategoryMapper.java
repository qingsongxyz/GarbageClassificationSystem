package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.GoodCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品种类表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Mapper
public interface GoodCategoryMapper extends BaseMapper<GoodCategory> {

    List<GoodCategory> getGoodCategoryList(@Param("category") String category, @Param("start") Integer start, @Param("number") Integer number);

    int getGoodCategoryCount(@Param("category") String category);
}
