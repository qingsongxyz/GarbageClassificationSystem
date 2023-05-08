package com.qingsongxyz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingsongxyz.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getUserById(long id);

    User getUserByUsername(String username);

    List<User> getAdminList();

    void decreaseUserScore(@Param("id") long id, @Param("number") int number);
}
