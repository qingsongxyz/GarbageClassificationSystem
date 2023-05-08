package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> getRolePathList(@Param("role") String role, @Param("path") String path);
}
