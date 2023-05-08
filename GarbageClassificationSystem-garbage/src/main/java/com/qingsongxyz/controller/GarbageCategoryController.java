package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Garbage;
import com.qingsongxyz.pojo.GarbageCategory;
import com.qingsongxyz.service.GarbageCategoryService;
import com.qingsongxyz.service.GarbageService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageCategoryVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 垃圾分类表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Api(tags = "垃圾分类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/garbage-category")
public class GarbageCategoryController {

    private final GarbageCategoryService garbageCategoryService;

    private final GarbageService garbageService;

    public GarbageCategoryController(GarbageCategoryService garbageCategoryService, GarbageService garbageService) {
        this.garbageCategoryService = garbageCategoryService;
        this.garbageService = garbageService;
    }

    @ApiOperation("测试添加垃圾分类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGarbageCategory(@Validated(CreateGroup.class) @RequestBody GarbageCategory garbageCategory) {
        long count = garbageCategoryService.count(new QueryWrapper<GarbageCategory>().eq("name", garbageCategory.getName()));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "该垃圾分类名称已存在, 添加分类失败!!!");
        }

        int result = garbageCategoryService.addGarbageCategory(garbageCategory);
        if (result > 0) {
            return CommonResult.ok("添加垃圾分类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加垃圾分类失败!!!");
    }

    @ApiOperation("测试通过id删除垃圾分类")
    @ApiImplicitParam(name = "id", value = "垃圾分类id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGarbageCategory(@Min(message = "垃圾分类id必须大于0", value = 1) @PathVariable("id") long id) {
        if (!deleteFlag(id)) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该分类的垃圾, 删除垃圾分类失败!!!");
        }
        int result = garbageCategoryService.deleteGarbageCategory(id);
        if (result > 0) {
            return CommonResult.ok("删除垃圾分类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除垃圾分类失败!!!");
    }

    @ApiOperation("测试批量删除垃圾分类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteGarbageCategoryList(@ValidListPositive(message = "垃圾分类id必须大于0") @RequestBody ValidList<Long> ids) {
        boolean success = true;
        List<Long> deletedList = new ArrayList<>();
        for (Long id : ids) {
            //跳过存在垃圾的分类
            if (!deleteFlag(id)) {
                success = false;
                continue;
            }
            deletedList.add(id);
        }
        int result = garbageCategoryService.deleteGarbageCategoryList(deletedList);
        if (!success) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该分类的垃圾, 删除垃圾分类失败!!!");
        }
        if (result > 0) {
            return CommonResult.ok("批量删除垃圾分类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除垃圾分类失败!!!");
    }

    @ApiOperation("测试通过id查询垃圾分类")
    @ApiImplicitParam(name = "id", value = "垃圾分类id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getGarbageCategoryById(@Min(message = "垃圾分类id必须大于0", value = 1) @PathVariable("id") long id) {
        GarbageCategoryVO garbageCategoryVO = garbageCategoryService.getGarbageCategoryById(id);
        return CommonResult.ok(garbageCategoryVO, "通过id查询垃圾分类信息成功!");
    }

    @ApiOperation("测试通过分类名称模糊查询所有垃圾分类信息")
    @ApiImplicitParam(name = "name", value = "垃圾分类名称", required = true, paramType = "query", dataTypeClass = String.class, example = "纸箱")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/name")
    public CommonResult getAllGarbageCategoryListByName(@NotBlank(message = "垃圾分类名称不能为空")
                                                        @Length(message = "垃圾分类称长度必须在1~50之间", min = 1, max = 50) String name) {

        List<GarbageCategoryVO> garbageCategoryVOList = garbageCategoryService.getAllGarbageCategoryListByName(name);
        return CommonResult.ok(garbageCategoryVOList, "通过分类名称查询垃圾分类信息成功!");
    }

    @ApiOperation("测试通过分类名称模糊分页查询垃圾分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "垃圾分类名称", required = true, paramType = "query", dataTypeClass = String.class, example = "纸箱"),
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
    @GetMapping("/list/name/{pageNum}/{pageSize}")
    public CommonResult getGarbageCategoryListByName(@NotBlank(message = "垃圾分类名称不能为空")
                                                     @Length(message = "垃圾分类称长度必须在1~50之间", min = 1, max = 50) String name,
                                                     @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                     @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageCategoryVO> garbageCategoryVOList = garbageCategoryService.getGarbageCategoryListByName(name, pageNum, pageSize);
        return CommonResult.ok(garbageCategoryVOList, "通过分类名称模糊分页查询垃圾分类信息成功!");
    }

    @ApiOperation("测试查询垃圾分类总数量")
    @ApiImplicitParam(name = "name", value = "垃圾分类名称", required = false, paramType = "query", dataTypeClass = String.class, example = "纸箱")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getGarbageCategoryCount(@Nullable String name) {
        long garbageCategoryCount = garbageCategoryService.getGarbageCategoryCount(name);
        return CommonResult.ok(garbageCategoryCount, "查询垃圾分类总数量成功!");
    }

    @ApiOperation("测试查询所有垃圾分类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/all")
    public CommonResult getAllGarbageCategoryList() {
        List<GarbageCategoryVO> allGarbageCategoryList = garbageCategoryService.getAllGarbageCategoryList();
        return CommonResult.ok(allGarbageCategoryList, "查询垃圾分类信息成功!");
    }

    @ApiOperation("测试分页查询垃圾分类")
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
    public CommonResult getGarbageCategoryList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                               @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageCategoryVO> garbageCategoryVOList = garbageCategoryService.getGarbageCategoryList(pageNum, pageSize);
        return CommonResult.ok(garbageCategoryVOList, "查询垃圾分类信息成功!");
    }

    @ApiOperation("测试修改垃圾分类信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateGarbageCategory(@Validated(UpdateGroup.class) @RequestBody GarbageCategory garbageCategory) {
        int result = garbageCategoryService.updateGarbageCategory(garbageCategory);
        if (result > 0) {
            return CommonResult.ok("修改垃圾分类信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改垃圾分类信息失败!!!");
    }

    /**
     * 查询是否存在该分类的垃圾
     *
     * @param id 分类id
     * @return 是否可以删除该分类
     */
    private boolean deleteFlag(long id) {
        List<Garbage> garbageList = garbageService.list(new QueryWrapper<Garbage>().eq("category_id", id));
        if (garbageList != null && garbageList.size() != 0) {
            return false;
        }
        return true;
    }
}
