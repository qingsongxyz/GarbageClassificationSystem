package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Broadcast;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 消息表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-17
 */
@Mapper
public interface BroadcastMapper extends BaseMapper<Broadcast> {

    Broadcast getBroadcastById(@Param("id") long id);

    List<Broadcast> getBroadcastList(@Param("title") String title, @Param("start") Integer start, @Param("number") Integer number);

    int getBroadcastListCount(@Param("title") String title);
}
