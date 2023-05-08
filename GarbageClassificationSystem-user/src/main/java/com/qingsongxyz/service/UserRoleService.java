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

}
