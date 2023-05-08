package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Path;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Mapper
public interface PathMapper extends BaseMapper<Path> {

    Path getPathDetailsById(long id);

    List<Path> getAllPathDetails();

    List<Path> getPathList(@Param("path") String path, @Param("start") Integer start, @Param("number") Integer number);

    int getPathCount(@Param("path") String path);
}
