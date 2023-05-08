package com.qingsongxyz.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.UserRole;
import com.qingsongxyz.service.UserRoleService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.ValidList;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;

/**
 * <p>
 * 用户角色表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Api(tags = "用户角色类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/user-role")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @ApiOperation("测试添加用户角色")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addUserRole(@Validated(CreateGroup.class) @RequestBody UserRole userRole) {
        UserRole ur = userRoleService.getOne(new QueryWrapper<UserRole>().eq("user_id", userRole.getUserId()));
        if(ObjectUtil.isNotNull(ur)){
            if(ur.getRoleId().equals(userRole.getRoleId())){
                return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "用户已拥有该角色, 添加角色失败!!!");
            }
        }
        int result = userRoleService.addUserRole(userRole);
        if (result > 0) {
            return CommonResult.ok("添加用户角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加用户角色失败!!!");
    }

    @ApiOperation("测试通过用户id和角色id删除用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{userId}/{roleId}")
    public CommonResult deleteUserRoleByUserIdAndRoleId(@Min(value = 1, message = "用户id必须是大于0的数字") @PathVariable("userId") long userId,
                                                        @Min(value = 1, message = "角色id必须是大于0的数字") @PathVariable("roleId") long roleId){
        int result = userRoleService.deleteUserRoleByUserIdAndRoleId(userId, roleId);
        if (result > 0) {
            return CommonResult.ok("通过用户id和角色id删除用户角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过用户id和角色id删除用户角色失败!!!");
    }

    @ApiOperation("测试通过id删除用户角色")
    @ApiImplicitParam(name = "id", value = "用户角色id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteUserRole(@Min(value = 1, message = "用户角色id必须是大于0的数字") @PathVariable("id") long id) {
        int result = userRoleService.deleteUserRole(id);
        if (result > 0) {
            return CommonResult.ok("通过id删除用户角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过id删除用户角色失败!!!");
    }

    @ApiOperation("测试批量删除用户角色")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteUserRoleList(@ValidListPositive(message = "用户角色id必须都大于0") @RequestBody ValidList<Long> ids) {
        int result = userRoleService.deleteUserRoleList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除用户角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除用户角色失败!!!");
    }
}
