package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<UserRole> getUserRoleListByUsername(@Param("username") String username);
}
