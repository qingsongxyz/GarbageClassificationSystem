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

    User getUserByUsername(@Param("username") String username);

    User getUser(@Param("username") String username, @Param("password") String password);

    User getUserByPhone(@Param("phone") String phone);

    User getUserByOpenid(@Param("openid") String openid);

    User getUserByAlipayid(@Param("alipayid") String alipayid);

    List<User> getAllUsersByName(@Param("username") String username);

    List<User> getUserListByRoleOrGenderOrName(@Param("role") String role, @Param("gender") String gender, @Param("username") String username, @Param("start")Integer start, @Param("number")Integer number);

    List<User> getUserList(@Param("start")Integer start, @Param("number")Integer number);

    int getCountByRoleOrGenderOrName(@Param("role") String role, @Param("gender") String gender, @Param("username") String username);
}
