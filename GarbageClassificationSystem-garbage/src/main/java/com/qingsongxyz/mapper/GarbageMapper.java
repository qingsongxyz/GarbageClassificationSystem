package com.qingsongxyz.mapper;

import com.qingsongxyz.pojo.Garbage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 垃圾信息表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Mapper
public interface GarbageMapper extends BaseMapper<Garbage> {

    Garbage getGarbageById(long id);

    List<Garbage> getGarbageList(@Param("garbageName")String garbageName, @Param("categoryName") String categoryName, @Param("start")Integer start, @Param("number")Integer number);

    List<Map<String, Object>> getGarbageCountGroupByCategory();
}
