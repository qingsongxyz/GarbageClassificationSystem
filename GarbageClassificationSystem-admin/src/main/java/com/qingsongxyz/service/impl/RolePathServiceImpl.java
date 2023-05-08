package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.RolePathMapper;
import com.qingsongxyz.pojo.RolePath;
import com.qingsongxyz.service.RolePathService;
import com.qingsongxyz.vo.RolePathVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Service
public class RolePathServiceImpl extends ServiceImpl<RolePathMapper, RolePath> implements RolePathService {

    private final RolePathMapper rolePathMapper;

    public RolePathServiceImpl(RolePathMapper rolePathMapper) {
        this.rolePathMapper = rolePathMapper;
    }

    @Override
    public int addRolePath(RolePath rolePath) {
        return rolePathMapper.insert(rolePath);
    }

    @Override
    public int deleteRolePathById(long id) {
        return rolePathMapper.deleteById(id);
    }

    @Override
    public int deleteRolePathList(List<Long> ids) {
        return rolePathMapper.deleteBatchIds(ids);
    }

    @Override
    public RolePathVO getRolePathById(long id) {
        RolePath rolePath = rolePathMapper.getRolePathById(id);
        RolePathVO rolePathVO = BeanUtil.copyProperties(rolePath, RolePathVO.class);
        return rolePathVO;
    }

    @Override
    public int updateRolePath(RolePath rolePath) {
        //允许角色id为null
        LambdaUpdateWrapper<RolePath> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(RolePath::getId, rolePath.getId());
        Long roleId = null;
        if(rolePath.getRoleId() != null){
            roleId = rolePath.getRoleId();
        }
        lambdaUpdateWrapper.set(RolePath::getRoleId, roleId);
        return rolePathMapper.update(null, lambdaUpdateWrapper);
    }

}
