package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.RolePath;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Mapper
public interface RolePathMapper extends BaseMapper<RolePath> {

    RolePath getRolePathById(long id);

}
