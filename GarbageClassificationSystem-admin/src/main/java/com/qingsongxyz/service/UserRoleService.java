package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.UserRole;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 添加用户角色
     * @param userRole 用户角色
     * @return 是否添加成功
     */
    int addUserRole(UserRole userRole);

    /**
     * 通过用户id和角色id删除用户角色
     * @param userId 用户id
     * @param roleId 角色id
     * @return 是否删除成功
     */
    int deleteUserRoleByUserIdAndRoleId(long userId, long roleId);

    /**
     * 通过id删除用户角色
     * @param id 用户角色id
     * @return 是否删除成功
     */
    int deleteUserRole(long id);

    /**
     * 批量删除用户角色
     * @param ids 用户角色id集合
     * @return 是否删除成功
     */
    int deleteUserRoleList(List<Long> ids);

    /**
     * 通过用户名查询用户角色信息
     * @param username 用户名
     * @return 用户角色信息
     */
    List<UserRole> getUserRoleListByUsername(String username);

}
