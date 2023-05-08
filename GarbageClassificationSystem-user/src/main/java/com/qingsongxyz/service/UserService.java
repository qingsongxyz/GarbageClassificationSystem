package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.CaptchaResult;
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
     * 账号密码注册用户
     *
     * @param captchaResult 验证码校验对象
     * @param user          用户
     * @return 是否注册成功
     */
    int register(CaptchaResult captchaResult, User user);

    //
    ///**
    // * 添加用户
    // *
    // * @param user 用户对象
    // * @return 是否添加成功
    // */
    //int addUser(User user);

    /**
     * 通过id查询用户信息
     *
     * @param id 用户信息
     * @return 用户信息
     */
    UserVO getUserById(long id);

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getUserByUsername(String username);

    /**
     * 更新用户排行榜
     */
    void loadUserRankingList();

    /**
     * 展示用户签到情况
     *
     * @param userId 用户id
     */
    String showSignInStatus(long userId);

    /**
     * 签到
     *
     * @param userId 用户id
     * @param score  添加的积分
     */
    void signIn(long userId, int score);

    /**
     * 查询用户积分排行榜
     *
     * @param start 起始
     * @return 用户积分排行榜
     */
    List<UserVO> getUserListByScoreRanking(int start);

    /**
     * 查询所有管理员用户
     *
     * @return 管理员用户列表
     */
    List<UserVO> getAdminList();

    /**
     * 修改密码
     *
     * @param captchaResult 验证码校验对象
     * @param id            用户id
     * @param oldPwd        旧密码
     * @param newPwd        新密码
     * @return 是否修改成功
     */
    int updatePwd(CaptchaResult captchaResult, long id, String oldPwd, String newPwd);

    /**
     * 修改用户信息
     *
     * @param user 用户
     * @return 是否修改用户信息
     */
    int updateUser(User user);

    /**
     * 扣减用户积分
     *
     * @param id     用户id
     * @param number 扣减积分数
     */
    void decreaseUserScore(long id, int number);

}
