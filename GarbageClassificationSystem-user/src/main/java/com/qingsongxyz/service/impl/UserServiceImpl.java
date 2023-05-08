package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.mapper.UserMapper;
import com.qingsongxyz.pojo.*;
import com.qingsongxyz.service.MarketService;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.service.UserRoleService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.vo.RoleVO;
import com.qingsongxyz.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.qingsongxyz.constant.RedisConstant.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    private final UserRoleService userRoleService;

    private final RoleService roleService;

    private final MarketService marketService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(UserMapper userMapper, UserRoleService userRoleService, RoleService roleService, MarketService marketService, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.marketService = marketService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int register(CaptchaResult captchaResult, User user) {
        String password = user.getPassword();
        String encodePwd = passwordEncoder.encode(password);
        user.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + encodePwd);
        user.setImage("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs/user.png");
        //1.添加用户
        int result = userMapper.insert(user);
        if (result > 0) {
            //查询游客角色id
            Role guest = roleService.getOne(new QueryWrapper<Role>().eq("role", "guest"));

            //2.给新注册的用户添加游客角色
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(guest.getId());
            result = userRoleService.addUserRole(userRole);
            if(result > 0){
                Market market = new Market();
                market.setUserId(user.getId());
                //3.添加用户购物车
                return marketService.addMarket(market);
            }
        }
        return result;
    }

    //@Transactional(rollbackFor = Exception.class)
    //@Override
    //public int addUser(User user) {
    //    String encodePwd = passwordEncoder.encode("123456");
    //    user.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + encodePwd);
    //    //1.添加用户
    //    int result = userMapper.insert(user);
    //    if (result > 0) {
    //        //查询游客角色id
    //        Role guest = roleService.getOne(new QueryWrapper<Role>().eq("role", "guest"));
    //
    //        //2.给新注册的用户添加游客角色
    //        UserRole userRole = new UserRole();
    //        userRole.setUserId(user.getId());
    //        userRole.setRoleId(guest.getId());
    //        result = userRoleService.addUserRole(userRole);
    //        if(result > 0){
    //            Market market = new Market();
    //            market.setUserId(user.getId());
    //            //3.添加用户购物车
    //            return marketService.addMarket(market);
    //        }
    //    }
    //    return result;
    //}

    @Override
    public UserVO getUserById(long id) {
        User user = userMapper.getUserById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        return userVO;
    }

    @Override
    public UserVO getUserByUsername(String username) {
        User user = userMapper.getUserByUsername(username);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        return userVO;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void loadUserRankingList() {
        log.info("定时任务开始,更新用户积分排行榜...");
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        //通过积分排序查询用户列表
        List<User> userList = list(new QueryWrapper<User>().eq("deleted", 0).gt("score", 0).orderByDesc("score").last("limit 99"));
        userList.forEach(u -> {
            UserVO userVO = BeanUtil.copyProperties(u, UserVO.class);

            //存入set key为JSON字符串 value为积分
            String str = JSON.toJSONString(userVO);
            double score = Double.parseDouble(userVO.getScore().toString());
            DefaultTypedTuple<String> tuple = new DefaultTypedTuple<>(str, score);
            set.add(tuple);
        });
        log.info("UserServiceImpl loadUserRankingList:{}", set);
        //存入redis Zset
        stringRedisTemplate.delete(USER_RANKING_LIST);
        stringRedisTemplate.opsForZSet().add(USER_RANKING_LIST, set);
    }

    @Override
    public String showSignInStatus(long userId) {
        //数组存储原始32位二进制签到数据
        byte[] arr = new byte[32];
        //获取今天是本月中的第几天
        LocalDate now = LocalDate.now();
        int dayOfMonth = now.getDayOfMonth();
        //获取用户本月到今天为止的签到情况
        List<Long> result = stringRedisTemplate.opsForValue().bitField(USER_SIGN_IN + ":" + userId,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
                        .valueAt(0));

        if (ObjectUtil.isNotEmpty(result)) {
            //获取签到结果
            Long sign = result.get(0);
            if(ObjectUtil.isNotNull(sign) && sign != 0){
                int temp = dayOfMonth;
                while (temp != 0) {
                    if ((sign & 1) == 1) {
                        arr[temp - 1] = 1;
                    }
                    temp--;
                    //右移一位
                    sign = sign >>> 1;
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (byte s : arr) {
            builder.append(s);
        }
        return builder.toString();
    }

    @Override
    public void signIn(long userId, int score) {
        //获取今天是本月中的第几天
        LocalDate now = LocalDate.now();
        int dayOfMonth = now.getDayOfMonth();
        log.info("UserServiceImpl signIn 今天是本月中的第几天:{}", dayOfMonth);
        if (dayOfMonth == 1) {
            stringRedisTemplate.delete(USER_SIGN_IN + ":" + userId);
        }
        //签到设置位为1
        stringRedisTemplate.opsForValue().setBit(USER_SIGN_IN + ":" + userId, dayOfMonth - 1, true);
        userMapper.decreaseUserScore(userId, (-1) * score);
    }

    @Override
    public List<UserVO> getUserListByScoreRanking(int start) {
        List<UserVO> userVOList = new ArrayList<>();
        Set<String> rankingSet = stringRedisTemplate.opsForZSet().reverseRange(USER_RANKING_LIST, start, start + 10);
        if (ObjectUtil.isNotEmpty(rankingSet)) {
            rankingSet.forEach(s -> {
                UserVO userVO = JSON.parseObject(s, UserVO.class);
                userVOList.add(userVO);
            });
        }
        return userVOList;
    }

    @Override
    public List<UserVO> getAdminList() {
        List<UserVO> userVOList = new ArrayList<>();
        List<User> adminList = userMapper.getAdminList();
        adminList.forEach(a -> {
            UserVO userVO = BeanUtil.copyProperties(a, UserVO.class);
            userVOList.add(userVO);
        });
        return userVOList;
    }

    @Override
    public int updatePwd(CaptchaResult captchaResult, long id, String oldPwd, String newPwd) {
        User user = userMapper.selectById(id);
        if (ObjectUtil.isNotNull(user)) {
            String encodeOldPwd = passwordEncoder.encode(oldPwd);
            String encodeNewPwd = passwordEncoder.encode(newPwd);
            if ((SecurityConstant.PASSWORD_BCRYPT_PREFIX + encodeOldPwd).equals(user.getPassword())) {
                User u = new User();
                u.setId(id);
                u.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + encodeNewPwd);
                return userMapper.updateById(u);
            }
        }
        return 0;
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public void decreaseUserScore(long id, int number) {
        userMapper.decreaseUserScore(id, number);
    }
}
