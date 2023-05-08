package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.pojo.*;
import com.qingsongxyz.service.feignService.MailFeignService;
import com.qingsongxyz.service.feignService.SmsFeignService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.UserVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import static com.qingsongxyz.constant.RedisConstant.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Api(tags = "用户类测试")
@Slf4j
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final StringRedisTemplate stringRedisTemplate;

    private final MailFeignService mailFeignService;

    private final SmsFeignService smsFeignService;

    public UserController(UserService userService, StringRedisTemplate stringRedisTemplate, @Qualifier("mailFeignService") MailFeignService mailFeignService, @Qualifier("smsFeignService") SmsFeignService smsFeignService) {
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailFeignService = mailFeignService;
        this.smsFeignService = smsFeignService;
    }

    @ApiOperation("测试账号密码注册用户")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/register")
    public CommonResult register(@Validated(CreateGroup.class) @RequestBody UserCaptcha userCaptcha) {
        long count = userService.count(new QueryWrapper<User>().eq("username", userCaptcha.getUser().getUsername()));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "用户名已存在, 注册失败!!!");
        }
        int result = userService.register(userCaptcha.getCaptchaResult(), userCaptcha.getUser());
        if (result > 0) {
            return CommonResult.ok("注册用户成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "注册用户失败!!!");
    }

    //
    //@ApiOperation("测试添加用户")
    //@ApiResponses({
    //        @ApiResponse(code = 200, message = "请求成功"),
    //        @ApiResponse(code = 400, message = "参数有误"),
    //        @ApiResponse(code = 401, message = "没有认证"),
    //        @ApiResponse(code = 403, message = "没有权限"),
    //        @ApiResponse(code = 500, message = "请求失败")
    //})
    //@PostMapping("/a")
    //public CommonResult addUser(@Validated(CreateGroup.class) @RequestBody User user) {
    //    long count = userService.count(new QueryWrapper<User>().eq("username", user.getUsername()));
    //    if (count > 0) {
    //        return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "用户名已存在, 添加失败!!!");
    //    }
    //    int result = userService.addUser(user);
    //    if (result > 0) {
    //        return CommonResult.ok("添加用户成功!");
    //    }
    //    return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加用户失败!!!");
    //}

    @ApiOperation("测试发送验证码短信")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, paramType = "query", dataTypeClass = String.class, example = "18327088164")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/phone/captcha")
    public CommonResult sendSms(@NotBlank(message = "手机号不能为空")
                                @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$") String phone){
        return smsFeignService.sendSms(phone);
    }

    @ApiOperation("测试发送绑定手机号验证码短信")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, paramType = "query", dataTypeClass = String.class, example = "18327088164")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/phone/captcha/binding")
    public CommonResult sendBindingSms(@NotBlank(message = "手机号不能为空")
                                       @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$") String phone){
        long count = userService.count(new QueryWrapper<User>().eq("phone", phone).eq("deleted", 0));
        if(count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "手机号已经绑定用户,无法重复绑定!!!");
        }
        return smsFeignService.sendSms(phone);
    }

    @ApiOperation("测试发送验证码邮件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/mail/captcha")
    public CommonResult mail(@RequestBody Mail mail){
        String targetMail = mail.getTargetMail();
        long count = userService.count(new QueryWrapper<User>().eq("email", targetMail).eq("deleted", 0));
        if(count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "邮箱已经绑定用户,无法重复绑定!!!");
        }
        //设置类型为验证码
        mail.setLetterType(LetterType.CAPTCHA.getId());
        return mailFeignService.mail(mail);
    }

    @ApiOperation("测试通过id查询用户信息")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getUserById(@Min(message = "用户id必须为大于0的数字", value = 1)
                                    @PathVariable("id") long id) {
        UserVO userVO = userService.getUserById(id);
        return CommonResult.ok(userVO, "通过id查询用户信息成功!");
    }

    @ApiOperation("测试通过用户名查询用户信息")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataTypeClass = String.class, example = "zhangsan")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/name")
    public CommonResult getUserByUsername(@NotBlank(message = "用户名不能为空")
                                          @Length(message = "用户名长度必须在1~50之间", min = 1, max = 50) String username) {
        UserVO userVO = userService.getUserByUsername(username);
        return CommonResult.ok(userVO, "通过用户名查询用户信息成功!");
    }

    @ApiOperation("测试查询用户积分排行榜")
    @ApiImplicitParam(name = "start", value = "起始下标", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/rank/{start}")
    public CommonResult getUserListByScoreRanking(@Min(value = 0, message = "起始下标必须为大于等于0的数字") @PathVariable("start") int start) {
        List<UserVO> userVOList = userService.getUserListByScoreRanking(start);
        return CommonResult.ok(userVOList, "查询用户积分排行榜成功!");
    }

    @ApiOperation("测试展示用户签到情况")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/signIn/{id}")
    public CommonResult showSignInStatus(@Min(message = "用户id必须为大于0的数字", value = 1)
                                         @PathVariable("id") long id) {
        String result = userService.showSignInStatus(id);
        return CommonResult.ok(result, "展示用户签到情况成功!");
    }

    @ApiOperation("测试签到")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "score", value = "增加的积分", required = true, paramType = "query", dataTypeClass = Integer.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/signIn/{id}")
    public CommonResult signIn(@Min(message = "用户id必须为大于0的数字", value = 1)
                               @PathVariable("id") long id,
                               @Min(message = "增加的积分必须为大于0的数字", value = 1) int score) {
        userService.signIn(id, score);
        return CommonResult.ok("签到成功!");
    }

    @ApiOperation("测试修改用户密码")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/pwd/{id}")
    public CommonResult updatePwd(@Validated @RequestBody UpdatePwdCaptcha updatePwdCaptcha) {
        int result = userService.updatePwd(updatePwdCaptcha.getCaptchaResult(), updatePwdCaptcha.getId(), updatePwdCaptcha.getOldPwd(), updatePwdCaptcha.getNewPwd());
        if (result > 0) {
            return CommonResult.ok("修改用户密码成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改用户密码失败!!!");
    }

    @ApiOperation("测试修改用户信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateUser(@Validated(UpdateGroup.class) @RequestBody User user) {
        int result = userService.updateUser(user);
        if (result > 0) {
            return CommonResult.ok("修改用户信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改用户信息失败!!!");
    }

    @ApiOperation("测试绑定邮箱")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/bind/email")
    public CommonResult bindingEmail(@Validated @RequestBody EmailCaptcha emailCaptcha) {
        String email = emailCaptcha.getEmail();
        String code = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY + ":" + email);
        String captcha = emailCaptcha.getCaptcha();
        if (!captcha.equals(code)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "验证码不一致!!!");
        }

        User user = new User();
        user.setId(emailCaptcha.getUserId());
        user.setEmail(email);

        int result = userService.updateUser(user);
        if (result > 0) {
            return CommonResult.ok("绑定邮箱成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "绑定邮箱失败!!!");
    }

    @ApiOperation("测试绑定手机号")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/bind/phone")
    public CommonResult bindingPhone(@Validated @RequestBody PhoneCaptcha phoneCaptcha) {
        String phone = phoneCaptcha.getPhone();
        String code = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY + ":" + phone);
        String captcha = phoneCaptcha.getCaptcha();
        if (!captcha.equals(code)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "验证码不一致!!!");
        }

        User user = new User();
        user.setId(phoneCaptcha.getUserId());
        user.setPhone(phone);

        int result = userService.updateUser(user);
        if (result > 0) {
            return CommonResult.ok("绑定手机号成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "绑定手机号失败!!!");
    }
}
