package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.vo.UserVO;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
public interface UserService extends IService<User> {

    /**
     * 添加用户
     * @param user 用户对象
     * @param roleId 角色id
     * @return 是否添加成功
     */
    int addUserAndRole(User user, long roleId);

    /**
     * 添加用户
     * @param user 用户对象
     * @return 是否添加成功
     */
    int addUser(User user);

    /**
     * 删除用户
     * @param id 用户id
     * @return 是否删除成功
     */
    int deleteUser(long id);

    /**
     * 批量删除用户
     * @param ids 用户id集合
     * @return 是否删除成功
     */
    int deleteUserList(List<Long> ids);

    /**
     * 通过用户名查询用户信息
     * @param username 用户名
     * @return 用户
     */
    UserDTO getUserByUsername(String username);

    /**
     * 通过用户名密码查询用户信息
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    UserDTO getUser(String username, String password);

    /**
     * 通过手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    UserDTO getUserByPhone(String phone);

    /**
     * 通过openid查询用户信息
     * @param openid 微信唯一表示
     * @return 用户信息
     */
    UserSerializableDTO getUserByOpenid(String openid);

    /**
     * 通过alipayid查询用户信息
     * @param alipayid 支付宝唯一表示
     * @return 用户信息
     */
    UserSerializableDTO getUserByAlipayid(String alipayid);

    /**
     * 通过用户名模糊查询所有用户信息
     * @param username 用户名
     * @return 所有用户信息
     */
    List<UserVO> getAllUsersByName(String username);

    /**
     * 通过角色英文名、性别和用户名模糊查询用户信息
     * @param role 角色英文名
     * @param gender 性别
     * @param username 用户名
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 用户集合
     */
    List<UserVO> getUserListByRoleOrGenderOrName(String role, String gender, String username, int pageNum, int pageSize);

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 用户集合
     */
    List<UserVO> getUserList(int pageNum, int pageSize);

    /**
     * 通过角色英文名、性别和用户名模糊查询用户数量
     * @param role 角色英文名
     * @param gender 性别
     * @param username 用户名
     * @return 用户数量
     */
    int getCountByRoleOrGenderOrName(String role, String gender, String username);

    /**
     * 修改用户信息
     * @param user 用户
     * @return 是否修改用户信息
     */
    int updateUser(User user);

}
