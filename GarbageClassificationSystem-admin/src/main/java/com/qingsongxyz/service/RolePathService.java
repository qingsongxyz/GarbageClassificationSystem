package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.RolePath;
import com.qingsongxyz.vo.RolePathVO;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
public interface RolePathService extends IService<RolePath> {

    /**
     * 添加角色权限信息
     * @param rolePath 角色权限对象
     * @return 是否添加成功
     */
    int addRolePath(RolePath rolePath);

    /**
     * 通过id删除角色权限信息
     * @param id 角色权限id
     * @return 是否删除成功
     */
    int deleteRolePathById(long id);

    /**
     * 批量删除角色权限列表
     * @param ids 角色权限id集合
     * @return 是否删除成功
     */
    int deleteRolePathList(List<Long> ids);

    /**
     * 通过id查询角色权限信息
     * @param id 角色权限id
     * @return 角色权限信息
     */
    RolePathVO getRolePathById(long id);

    /**
     * 修改角色权限信息
     * @param rolePath 角色权限对象
     * @return 是否修改成功
     */
    int updateRolePath(RolePath rolePath);
}
