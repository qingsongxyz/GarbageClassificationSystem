package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.RolePath;
import com.qingsongxyz.service.RolePathService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.RolePathVO;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;


/**
 * <p>
 * 角色权限表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Api(tags = "角色权限类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/role-path")
public class RolePathController {

    private final RolePathService rolePathService;

    public RolePathController(RolePathService rolePathService) {
        this.rolePathService = rolePathService;
    }

    @ApiOperation("测试添加角色权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pathId", value = "权限id", required = true, paramType = "query", dataTypeClass = String.class, example = "1"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a/{pathId}/{roleId}")
    public CommonResult addRolePath(@Min(message = "权限id必须为大于0的数字", value = 1)
                                    @PathVariable("pathId") long pathId,
                                    @Min(message = "角色id必须为大于0的数字", value = 1)
                                    @PathVariable("roleId") long roleId){
        long count = rolePathService.count(new QueryWrapper<RolePath>().eq("role_id", roleId).eq("path_id", pathId));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "该角色权限信息已存在,无法重复添加!!!");
        }
        RolePath rolePath = new RolePath();
        rolePath.setPathId(pathId);
        rolePath.setRoleId(roleId);
        int result = rolePathService.addRolePath(rolePath);
        if (result > 0) {
            return CommonResult.ok("添加角色权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加角色权限信息失败!!!");
    }

    @ApiOperation("测试通过id删除角色权限信息")
    @ApiImplicitParam(name = "id", value = "角色权限id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteRolePathById(@Min(value = 1, message = "角色权限id必须是大于0的数字") @PathVariable("id") long id) {
        int result = rolePathService.deleteRolePathById(id);
        if (result > 0) {
            return CommonResult.ok("通过id删除角色权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "通过id删除角色权限信息失败!!!");
    }

    @ApiOperation("测试批量删除角色权限列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteRolePathList(@ValidListPositive(message = "角色权限id必须都大于0") @RequestBody ValidList<Long> ids) {
        int result = rolePathService.deleteRolePathList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除角色权限列表成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除角色权限列表失败!!!");
    }

    @ApiOperation("测试通过id查询角色权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getRolePathById(@Min(value = 1, message = "角色权限id必须是大于0的数字") @PathVariable("id") long id){
        RolePathVO rolePathVO = rolePathService.getRolePathById(id);
        return CommonResult.ok(rolePathVO, "通过id查询角色权限信息成功!");
    }

    @ApiOperation("测试修改角色权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateRolePath(@Validated({UpdateGroup.class}) @RequestBody RolePath rolePath) {
        int result = rolePathService.updateRolePath(rolePath);
        if (result > 0) {
            return CommonResult.ok("修改角色权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改角色权限信息失败!!!");
    }
}
