package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.*;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.service.UserRoleService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.RoleVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Api(tags = "角色类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    public RoleController(RoleService roleService, UserRoleService userRoleService) {
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    @ApiOperation("测试添加角色")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addRole(@Validated(CreateGroup.class) @RequestBody Role role) {
        long count = roleService.count(new QueryWrapper<Role>().eq("role", role.getRole()));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "该角色英文名已存在, 添加角色失败!!!");
        }
        int result = roleService.addRole(role);
        if (result > 0) {
            return CommonResult.ok("添加角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加角色失败!!!");
    }

    @ApiOperation("测试通过id删除角色")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteRole(@Min(value = 1, message = "角色id必须是大于0的数字") @PathVariable("id") long id) {
        if (!deleteFlag(id)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该角色的用户, 删除角色失败!!!");
        }
        int result = roleService.deleteRole(id);
        if (result > 0) {
            return CommonResult.ok("删除角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除角色失败!!!");
    }

    @ApiOperation("测试批量删除角色集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteRoleList(@ValidListPositive(message = "角色id必须都大于0") @RequestBody ValidList<Long> ids) {
        boolean success = true;
        for (Long id : ids) {
            if (!deleteFlag(id)) {
                success = false;
                continue;
            }
            int result = roleService.deleteRole(id);
            if (result == 0) {
                return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除垃圾分类失败!!!");
            }
        }
        if (success) {
            return CommonResult.ok("批量删除角色成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该角色的用户, 批量删除角色失败!!!");
    }

    @ApiOperation("测试查询角色集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list")
    public CommonResult getRoleList() {
        List<RoleVO> roleVOList = roleService.getRoleList();
        return CommonResult.ok(roleVOList, "查询角色集合成功!");
    }

    @ApiOperation("测试通过角色英文名模糊查询")
    @ApiImplicitParam(name = "role", value = "角色英文名", required = true, paramType = "query", dataTypeClass = String.class, example = "admin")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/role")
    public CommonResult getRoleListByRole(@NotBlank(message = "角色英文名不能为空", groups = {CreateGroup.class})
                                          @Length(message = "角色英文名长度必须在1~50之间", min = 1, max = 50) String role) {
        List<RoleVO> roleVOList = roleService.getRoleListByRole(role);
        return CommonResult.ok(roleVOList, "通过角色英文名模糊查询成功!");
    }

    @ApiOperation("测试查询角色权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/rolePathList")
    public CommonResult getRolePathList() {
        List<RoleVO> roleVOList = roleService.getRolePathList();
        return CommonResult.ok(roleVOList, "查询用户权限信息成功!");
    }

    @ApiOperation("测试通过角色英文名和权限路径查询角色权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "角色英文名", required = false, paramType = "query", dataTypeClass = String.class, example = "admin"),
            @ApiImplicitParam(name = "path", value = "权限名称", required = false, paramType = "query", dataTypeClass = String.class, example = "/path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/rolePathList/role/path")
    public CommonResult getAllRolePathByRoleAndPath(@Nullable String role,
                                                    @Nullable String path) {
        List<RoleVO> roleVOList = roleService.getAllRolePathByRoleAndPath(role, path);
        return CommonResult.ok(roleVOList, "通过角色英文名和权限路径查询角色权限列表成功!");
    }

    @ApiOperation("测试通过权限路径查询角色权限列表")
    @ApiImplicitParam(name = "path", value = "权限路径", required = true, paramType = "query", dataTypeClass = String.class, example = "/path")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/rolePathList/path")
    public CommonResult getAllRolePathByPath(@NotBlank(message = "权限路径不能为空")
                                             @Length(message = "权限路径长度必须在1~255之间", min = 1, max = 255) String path) {
        List<RoleVO> roleVOList = roleService.getAllRolePathByPath(path);
        return CommonResult.ok(roleVOList, "通过权限路径查询角色权限列表成功!");
    }

    @ApiOperation("测试修改角色信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateRole(@Validated(UpdateGroup.class) @RequestBody Role role) {
        long count = roleService.count(new QueryWrapper<Role>().eq("role", role.getRole()));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "修改角色的英文名已存在, 修改角色信息失败!!!");
        }
        Role guest = roleService.getOne(new QueryWrapper<Role>().eq("role", "guest"));
        if(guest.getId().equals(role.getId())){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "不能修改游客的角色英文名, 修改角色信息失败!!!");
        }
        int result = roleService.updateRole(role);
        if (result > 0) {
            return CommonResult.ok("修改角色信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改角色信息失败!!!");
    }

    /**
     * @param id 角色id
     * @return 是否可以删除该角色
     */
    private boolean deleteFlag(long id) {
        List<UserRole> userRoleList = userRoleService.list(new QueryWrapper<UserRole>().eq("role_id", id));
        if (userRoleList != null && userRoleList.size() != 0) {
            return false;
        }
        return true;
    }
}
