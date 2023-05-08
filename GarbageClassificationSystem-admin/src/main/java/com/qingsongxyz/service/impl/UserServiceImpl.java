package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.mapper.UserMapper;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.pojo.UserRole;
import com.qingsongxyz.service.MarketService;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.service.UserRoleService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.vo.RoleVO;
import com.qingsongxyz.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    private final UserRoleService userRoleService;

    private final MarketService marketService;

    private final RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, UserRoleService userRoleService, MarketService marketService, RoleService roleService) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
        this.marketService = marketService;
        this.roleService = roleService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addUserAndRole(User user, long roleId) {
        //设置默认密码123456 bcrypt加密存储
        String pwd = passwordEncoder.encode("123456");
        user.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + pwd);
        //1.添加用户
        int result = userMapper.insert(user);
        if (result > 0) {
            //给用户添加对应的角色
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            //2.添加用户角色
            result = userRoleService.addUserRole(userRole);
            if(result > 0){
                //3.添加用户购物车
                return marketService.addMarket(user.getId());
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addUser(User user) {
        String encodePwd = passwordEncoder.encode("123456");
        user.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + encodePwd);
        //1.添加用户
        int result = userMapper.insert(user);
        if (result > 0) {
            //查询游客角色id
            RoleVO guest = roleService.getRoleListByRole("guest").get(0);

            //2.给新注册的用户添加游客角色
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(guest.getId());
            result = userRoleService.addUserRole(userRole);
            if(result > 0){
                //3.添加用户购物车
                return marketService.addMarket(user.getId());
            }
        }
        return result;
    }

    @Override
    public int deleteUser(long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int deleteUserList(List<Long> ids) {
        return userMapper.deleteBatchIds(ids);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userMapper.getUserByUsername(username);
        UserDTO userDTO = null;
        if (ObjectUtil.isNotNull(user)) {
            userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            userDTO.setRoleList(user.getRoleList());
        }
        return userDTO;
    }

    @Override
    public UserDTO getUser(String username, String password) {
        User user = userMapper.getUser(username, password);
        UserDTO userDTO = null;
        if (ObjectUtil.isNotNull(user)) {
            userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            userDTO.setRoleList(user.getRoleList());
        }
        return userDTO;
    }

    @Override
    public UserDTO getUserByPhone(String phone) {
        User user = userMapper.getUserByPhone(phone);
        UserDTO userDTO = null;
        if (ObjectUtil.isNotNull(user)) {
            userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            userDTO.setRoleList(user.getRoleList());
        }
        return userDTO;
    }

    @Override
    public UserSerializableDTO getUserByOpenid(String openid) {
        User user = userMapper.getUserByOpenid(openid);
        UserSerializableDTO userDTO = null;
        if (ObjectUtil.isNotNull(user)) {
            userDTO = BeanUtil.copyProperties(user, UserSerializableDTO.class);
            userDTO.setRoleList(user.getRoleList());
        }
        return userDTO;
    }

    @Override
    public UserSerializableDTO getUserByAlipayid(String alipayid) {
        User user = userMapper.getUserByAlipayid(alipayid);
        UserSerializableDTO userDTO = null;
        if (ObjectUtil.isNotNull(user)) {
            userDTO = BeanUtil.copyProperties(user, UserSerializableDTO.class);
            userDTO.setRoleList(user.getRoleList());
        }
        return userDTO;
    }

    @Override
    public List<UserVO> getAllUsersByName(String username) {
        List<User> userList = userMapper.getAllUsersByName(username);
        return transfer(userList);
    }

    @Override
    public List<UserVO> getUserListByRoleOrGenderOrName(String role, String gender, String username, int pageNum, int pageSize) {
        List<User> userList = userMapper.getUserListByRoleOrGenderOrName(role, gender, username, (pageNum - 1) * pageSize, pageSize);
        return transfer(userList);
    }

    @Override
    public List<UserVO> getUserList(int pageNum, int pageSize) {
        List<User> userList = userMapper.getUserList((pageNum - 1) * pageSize, pageSize);
        return transfer(userList);
    }

    @Override
    public int getCountByRoleOrGenderOrName(String role, String gender, String username) {
        return userMapper.getCountByRoleOrGenderOrName(role, gender, username);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    /**
     * 将用户集合转换为VO集合
     *
     * @param userList 用户集合
     * @return VO集合
     */
    private List<UserVO> transfer(List<User> userList) {
        List<UserVO> userVOList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(userList)) {
            userList.forEach(u -> {
                UserVO userVO = BeanUtil.copyProperties(u, UserVO.class);
                userVOList.add(userVO);
            });
        }
        return userVOList;
    }
}
