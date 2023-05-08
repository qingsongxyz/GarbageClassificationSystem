package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.RoleMapper;
import com.qingsongxyz.pojo.Role;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.vo.RoleVO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public int addRole(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int deleteRole(long id) {
        return roleMapper.deleteById(id);
    }

    @Override
    public int deleteRoleList(List<Long> ids) {
        return roleMapper.deleteBatchIds(ids);
    }

    @Override
    public List<RoleVO> getRoleList() {
        return transfer(this.list());
    }

    @Override
    public List<RoleVO> getRoleListByRole(String role) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.like("role", role);
        List<Role> roleList = this.list(wrapper);
        return transfer(roleList);
    }

    @Override
    public List<RoleVO> getRolePathList() {
        List<Role> roleList = roleMapper.getRolePathList(null, null);
        return transfer(roleList);
    }

    @Override
    public List<RoleVO> getAllRolePathByRoleAndPath(String role, String path) {
        List<Role> roleList = roleMapper.getRolePathList(role, path);
        return transfer(roleList);
    }

    @Override
    public List<RoleVO> getAllRolePathByPath(String path) {
        List<Role> roleList = roleMapper.getRolePathList(null, path);
        return transfer(roleList);
    }

    @Override
    public int updateRole(Role role) {
        return roleMapper.updateById(role);
    }

    /**
     * 将角色集合转化为VO对象集合
     * @param roleList 角色集合
     * @return VO对象集合
     */
    private List<RoleVO> transfer(List<Role> roleList){
        List<RoleVO> roleVOList = new ArrayList<>();
        roleList.forEach(r -> {
            RoleVO roleVO = BeanUtil.copyProperties(r, RoleVO.class);
            roleVOList.add(roleVO);
        });
        return roleVOList;
    }

}
