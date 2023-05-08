package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Role;
import com.qingsongxyz.vo.RoleVO;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
public interface RoleService extends IService<Role> {

    /**
     * 添加角色
     * @param role 角色
     * @return 是否添加成功
     */
    int addRole(Role role);

    /**
     * 通过id删除角色
     * @param id 角色id
     * @return 是否删除成功
     */
    int deleteRole(long id);

    /**
     * 批量删除角色集合
     * @param ids 角色id集合
     * @return 是否删除成功
     */
    int deleteRoleList(List<Long> ids);

    /**
     * 查询所有角色
     * @return 角色集合
     */
    List<RoleVO> getRoleList();

    /**
     * 通过角色英文名模糊查询
     * @param role 角色英文名
     * @return 角色集合
     */
    List<RoleVO> getRoleListByRole(String role);

    /**
     * 查询角色权限列表
     * @return 角色集合
     */
    List<RoleVO> getRolePathList();

    /**
     * 通过角色英文名和权限路径查询角色权限列表
     * @param role 角色英文名
     * @param path 权限路径
     * @return 角色集合
     */
    List<RoleVO> getAllRolePathByRoleAndPath(String role, String path);

    /**
     * 通过权限路径查询角色权限列表
     * @param path 权限路径
     * @return 角色集合
     */
    List<RoleVO> getAllRolePathByPath(String path);

    /**
     * 修改角色信息
     * @param role 角色
     * @return 是否修改成功
     */
    int updateRole(Role role);
}
