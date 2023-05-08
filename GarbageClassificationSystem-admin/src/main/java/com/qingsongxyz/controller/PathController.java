package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Path;
import com.qingsongxyz.pojo.Role;
import com.qingsongxyz.pojo.RolePath;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.service.RolePathService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.PathVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Api(tags = "权限类测试")
@RestController
@Slf4j
@Validated //非pojo参数检验声明
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;

    private final RolePathService rolePathService;

    public PathController(PathService pathService, RolePathService rolePathService) {
        this.pathService = pathService;
        this.rolePathService = rolePathService;
    }

    @ApiOperation("测试添加权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addPath(@Validated({CreateGroup.class}) @RequestBody Path path) {
        long count = pathService.count(new QueryWrapper<Path>().eq("path", path.getPath()));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "该权限路径名已存在, 添加权限信息失败!!!");
        }
        int result = pathService.addPath(path);
        if (result > 0) {
            return CommonResult.ok("添加权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加权限信息失败!!!");
    }

    @ApiOperation("测试删除权限信息")
    @ApiImplicitParam(name = "id", value = "权限id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deletePath(@Min(value = 1, message = "权限id必须是大于0的数字") @PathVariable("id") long id) {
        long count = rolePathService.count(new QueryWrapper<RolePath>().eq("path_id", id));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在拥有该权限的角色, 删除失败!!!");
        }
        int result = pathService.deletePath(id);
        if (result > 0) {
            return CommonResult.ok("删除权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除权限信息失败!!!");
    }

    @ApiOperation("测试批量删除权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deletePathList(@ValidListPositive(message = "权限id必须都大于0") @RequestBody ValidList<Long> ids) {
        boolean success = true;
        ArrayList<Long> deletedList = new ArrayList<>();
        for (Long id : ids) {
            long count = rolePathService.count(new QueryWrapper<RolePath>().eq("path_id", id));
            if (count > 0) {
                success = false;
                continue;
            }
            deletedList.add(id);
        }
        int result = pathService.deletePathList(deletedList);
        if (!success) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在拥有该权限的角色, 删除失败!!!");
        }
        if (result > 0) {
            return CommonResult.ok("批量删除权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除权限信息失败!!!");
    }

    @ApiOperation("测试查询所有角色权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/details")
    public CommonResult getAllPathDetails() {
        List<PathVO> pathVOList = pathService.getAllPathDetails();
        return CommonResult.ok(pathVOList, "查询所有角色权限信息成功!");
    }

    @ApiOperation("测试查询所有权限集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list")
    public CommonResult getAllPathList(){
        List<PathVO> pathVOList = pathService.getAllPathList();
        return CommonResult.ok(pathVOList, "查询所有权限集合成功!");
    }

    @ApiOperation("测试分页查询权限集合")
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
    public CommonResult getPathList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                    @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<PathVO> pathVOList = pathService.getPathList(pageNum, pageSize);
        return CommonResult.ok(pathVOList, "分页查询权限集合成功!");
    }

    @ApiOperation("测试通过权限路径模糊分页查询权限集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "权限路径", required = true, paramType = "query", dataTypeClass = String.class, example = "/path"),
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
    @GetMapping("/list/path/{pageNum}/{pageSize}")
    public CommonResult getPathListByPath(@NotBlank(message = "权限路径不能为空")
                                          @Length(message = "权限路径长度必须在1~255之间", min = 1, max = 255) String path,
                                          @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                          @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<PathVO> pathVOList = pathService.getPathListByPath(path, pageNum, pageSize);
        return CommonResult.ok(pathVOList, "通过权限路径模糊分页查询权限集合成功!");
    }

    @ApiOperation("测试通过权限路径模糊查询所有权限集合")
    @ApiImplicitParam(name = "path", value = "权限路径", required = true, paramType = "query", dataTypeClass = String.class, example = "/path")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/path")
    public CommonResult getAllPathListByPath(@NotBlank(message = "权限路径不能为空")
                                             @Length(message = "权限路径长度必须在1~255之间", min = 1, max = 255) String path) {
        List<PathVO> pathVOList = pathService.getAllPathListByPath(path);
        return CommonResult.ok(pathVOList, "通过权限路径模糊查询所有权限集合成功!");
    }

    @ApiOperation("测试查询权限总数量")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllPathCount() {
        int count = pathService.getAllPathCount();
        return CommonResult.ok(count, "查询权限总数量成功!");
    }

    @ApiOperation("测试通过权限路径模糊查询数量")
    @ApiImplicitParam(name = "path", value = "权限路径", required = true, paramType = "query", dataTypeClass = String.class, example = "/path")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/path")
    public CommonResult getPathCountByPath(@NotBlank(message = "权限路径不能为空")
                                           @Length(message = "权限路径长度必须在1~255之间", min = 1, max = 255) String path) {
        int count = pathService.getPathCountByPath(path);
        return CommonResult.ok(count, "通过权限路径模糊查询数量成功!");
    }

    @ApiOperation("测试修改权限信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updatePath(@Validated({UpdateGroup.class}) @RequestBody Path path) {
        long count = pathService.count(new QueryWrapper<Path>().eq("path", path.getPath()));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "修改的权限路径已存在, 修改权限信息失败!!!");
        }
        int result = pathService.updatePath(path);
        if (result > 0) {
            return CommonResult.ok("修改权限信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改权限信息失败!!!");
    }
}
