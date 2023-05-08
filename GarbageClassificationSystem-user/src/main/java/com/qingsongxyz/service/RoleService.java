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
     * 通过角色英文名模糊查询
     * @param role 角色英文名
     * @return 角色集合
     */
    List<RoleVO> getRoleListByRole(String role);

}
