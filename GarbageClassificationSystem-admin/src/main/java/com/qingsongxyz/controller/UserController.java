package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.service.impl.CanalServiceImpl;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.UserVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Api(tags = "用户类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final CanalServiceImpl canalService;

    public UserController(UserService userService, CanalServiceImpl canalService) {
        this.userService = userService;
        this.canalService = canalService;
    }

    @PostConstruct
    void listen() {
        try {
            canalService.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("测试添加用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataTypeClass = String.class, example = "zhangsan")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a/{roleId}")
    public CommonResult addUserAndRole(@Min(value = 1, message = "角色id必须是大于0的数字")
                                       @PathVariable("roleId") long roleId,
                                       @NotBlank(message = "用户名不能为空")
                                       @Length(message = "用户名长度必须在1~50之间", min = 6, max = 20) String username) {
        long count = userService.count(new QueryWrapper<User>().eq("username", username));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "用户名已存在, 注册失败!!!");
        }
        User user = new User();
        user.setUsername(username);
        int result = userService.addUserAndRole(user, roleId);
        if (result > 0) {
            return CommonResult.ok("注册用户成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "注册用户失败!!!");
    }

    @ApiOperation("测试删除用户")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteUser(@Min(value = 1, message = "用户id必须是大于0的数字") @PathVariable("id") long id) {
        int result = userService.deleteUser(id);
        if (result > 0) {
            return CommonResult.ok("删除用户成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除用户失败!!!");
    }

    @ApiOperation("测试批量删除用户")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteUserList(@ValidListPositive(message = "用户id必须都大于0") @RequestBody ValidList<Long> ids) {
        int result = userService.deleteUserList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除用户成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除用户失败!!!");
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
                                          @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20)
                                          @Pattern(regexp = "^\\w+$", message = "用户名只能包含数字、英文字符和下划线") String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        return CommonResult.ok(userDTO, "通过用户名查询用户信息成功!");
    }

    @ApiOperation("测试通过手机号查询用户信息")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, paramType = "query", dataTypeClass = String.class, example = "188327088164")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/phone")
    public CommonResult getUserByPhone(@NotBlank(message = "手机号不能为空")
                                       @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$") String phone) {
        UserDTO userDTO = userService.getUserByPhone(phone);
        return CommonResult.ok(userDTO, "通过手机号查询用户信息成功!");
    }

    @ApiOperation("测试通过openid查询用户信息")
    @ApiImplicitParam(name = "openid", value = "微信唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/openid")
    public CommonResult getUserByOpenid(@NotBlank(message = "openid不能为空") String openid) {
        UserSerializableDTO userDTO = userService.getUserByOpenid(openid);
        return CommonResult.ok(userDTO, "通过openid查询用户信息成功!");
    }

    @ApiOperation("测试通过alipayid查询用户信息")
    @ApiImplicitParam(name = "alipayid", value = "支付宝唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/alipayid")
    public CommonResult getUserByAlipayid(@NotBlank(message = "alipayid不能为空") String alipayid) {
        UserSerializableDTO userDTO = userService.getUserByAlipayid(alipayid);
        return CommonResult.ok(userDTO, "通过alipayid查询用户信息成功!");
    }

    @ApiOperation("测试通过用户名模糊查询所有用户信息")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataTypeClass = String.class, example = "zhangsan")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/all/name")
    public CommonResult getAllUsersByName(@NotBlank(message = "用户名不能为空")
                                          @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20)
                                          @Pattern(regexp = "^\\w+$", message = "用户名只能包含数字、英文字符和下划线") String username) {
        List<UserVO> userVOList = userService.getAllUsersByName(username);
        return CommonResult.ok(userVOList, "通过用户名模糊查询所有用户信息成功!");
    }

    @ApiOperation("测试通过角色英文名、性别和用户名模糊查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页面容量", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "role", value = "用户角色", required = false, dataType = "query", dataTypeClass = String.class, example = "admin"),
            @ApiImplicitParam(name = "gender", value = "性别", required = false, dataType = "query", dataTypeClass = String.class, example = "男"),
            @ApiImplicitParam(name = "username", value = "用户名", required = false, dataType = "query", dataTypeClass = String.class, example = "zhangsan")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/role/name/{pageNum}/{pageSize}")
    public CommonResult getUserListByRoleOrGenderOrName(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                        @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize,
                                                        @Nullable String role,
                                                        @Nullable String gender,
                                                        @Nullable String username) {
        List<UserVO> userVOList = userService.getUserListByRoleOrGenderOrName(role, gender, username, pageNum, pageSize);
        return CommonResult.ok(userVOList, "通过角色英文名和用户名模糊查询用户信息成功!");
    }

    @ApiOperation("测试分页查询用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页面容量", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/{pageNum}/{pageSize}")
    public CommonResult getUserList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                    @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<UserVO> userVOList = userService.getUserList(pageNum, pageSize);
        return CommonResult.ok(userVOList, "分页查询用户列表成功!");
    }

    @ApiOperation("通过角色英文名、性别和用户名模糊查询用户数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "用户角色", required = false, paramType = "query", dataTypeClass = String.class, example = "admin"),
            @ApiImplicitParam(name = "gender", value = "性别", required = false, paramType = "query", dataTypeClass = String.class, example = "男"),
            @ApiImplicitParam(name = "username", value = "用户名", required = false, paramType = "query", dataTypeClass = String.class, example = "zhangsan")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/role/name")
    public CommonResult getCountByRoleOrGenderOrName(@Nullable String role,
                                                     @Nullable String gender,
                                                     @Nullable String username) {
        int count = userService.getCountByRoleOrGenderOrName(role, gender, username);
        return CommonResult.ok(count, "通过角色英文名、性别和用户名模糊查询用户数量成功!");
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
}
