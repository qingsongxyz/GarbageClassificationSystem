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
    public List<RoleVO> getRoleListByRole(String role) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.like("role", role);
        List<Role> roleList = this.list(wrapper);
        return transfer(roleList);
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
