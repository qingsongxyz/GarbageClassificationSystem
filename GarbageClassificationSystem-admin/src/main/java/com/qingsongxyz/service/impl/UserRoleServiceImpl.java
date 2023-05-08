package com.qingsongxyz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.UserRoleMapper;
import com.qingsongxyz.pojo.UserRole;
import com.qingsongxyz.service.UserRoleService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

    public UserRoleServiceImpl(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public int addUserRole(UserRole userRole) {
        return userRoleMapper.insert(userRole);
    }

    @Override
    public int deleteUserRoleByUserIdAndRoleId(long userId, long roleId) {
        return userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId).eq("role_id", roleId));
    }

    @Override
    public int deleteUserRole(long id) {
        return userRoleMapper.deleteById(id);
    }

    @Override
    public int deleteUserRoleList(List<Long> ids) {
        return userRoleMapper.deleteBatchIds(ids);
    }

    @Override
    public List<UserRole> getUserRoleListByUsername(String username) {
        return userRoleMapper.getUserRoleListByUsername(username);
    }
}
