package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Garbage;
import com.qingsongxyz.service.GarbageService;
import com.qingsongxyz.service.impl.CanalServiceImpl;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 垃圾信息表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Api(tags = "垃圾类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/garbage")
public class GarbageController {

    private final GarbageService garbageService;

    private final CanalServiceImpl canalService;

    public GarbageController(GarbageService garbageService, CanalServiceImpl canalService) {
        this.garbageService = garbageService;
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

    @ApiOperation("测试添加垃圾")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGarbage(@Validated(CreateGroup.class) @RequestBody Garbage garbage) {
        long count = garbageService.count(new QueryWrapper<Garbage>().eq("name", garbage.getName()));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "该垃圾名称已存在, 添加垃圾失败!!!");
        }

        int result = garbageService.addGarbage(garbage);
        if (result > 0) {
            return CommonResult.ok("添加垃圾成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加垃圾失败!!!");
    }

    @ApiOperation("测试通过id删除垃圾")
    @ApiImplicitParam(name = "id", value = "垃圾id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGarbage(@Min(message = "垃圾id必须大于0", value = 1) @PathVariable("id") long id) {
        int result = garbageService.deleteGarbage(id);
        if (result > 0) {
            return CommonResult.ok("删除垃圾成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除垃圾失败!!!");
    }

    @ApiOperation("测试批量删除垃圾")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteGarbageList(@ValidListPositive(message = "垃圾id必须都大于0") @RequestBody ValidList<Long> ids) {
        int result = garbageService.deleteGarbageList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除垃圾成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除垃圾失败!!!");
    }

    @ApiOperation("测试通过关键词查询所有垃圾信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = true, paramType = "query", dataTypeClass = String.class, example = "盒"),
            @ApiImplicitParam(name = "isHighlight", value = "是否需要高亮", required = false, paramType = "query", dataTypeClass = String.class, example = "true")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/keyword")
    public CommonResult getAllGarbageByKeyword(@NotBlank(message = "关键词不能为空") String keyword, @RequestParam(value = "isHighlight", required = false, defaultValue = "true") String isHighlight) throws IOException {
        List<GarbageVO> garbageList = garbageService.getAllGarbageByKeyword(keyword, isHighlight);
        return CommonResult.ok(garbageList, "通过关键词查询所有垃圾信息成功!");
    }

    @ApiOperation("测试通过关键词分页查询垃圾信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = true, dataType = "query", dataTypeClass = String.class, example = "盒"),
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
    @GetMapping("/keyword/{pageNum}/{pageSize}")
    public CommonResult getGarbageByKeyword(@NotBlank(message = "关键词不能为空") String keyword,
                                            @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                            @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) throws IOException {
        List<GarbageVO> garbageList = garbageService.getGarbageByKeyword(keyword, pageNum, pageSize);
        return CommonResult.ok(garbageList, "通过关键词分页查询垃圾信息成功!");
    }

    @ApiOperation("测试通过关键词查询垃圾总数量")
    @ApiImplicitParam(name = "keyword", value = "关键词", required = true, paramType = "query", dataTypeClass = String.class, example = "盒")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/keyword")
    public CommonResult getGarbageCountByKeyword(@NotBlank(message = "关键词不能为空") String keyword) throws IOException {
        long count = garbageService.getGarbageCountByKeyword(keyword);
        return CommonResult.ok(count, "通过关键词查询垃圾总数量成功!");
    }

    @ApiOperation("测试通过id查询垃圾信息")
    @ApiImplicitParam(name = "id", value = "垃圾id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getGarbageById(@Min(message = "垃圾id必须大于0", value = 1) @PathVariable("id") long id) {
        GarbageVO garbage = garbageService.getGarbageById(id);
        return CommonResult.ok(garbage, "通过id查询垃圾信息成功!");
    }

    @ApiOperation("测试分页查询垃圾信息")
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
    public CommonResult getGarbageList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                       @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageVO> garbageList = garbageService.getGarbageList(pageNum, pageSize);
        return CommonResult.ok(garbageList, "查询垃圾信息成功!");
    }

    @ApiOperation("测试通过垃圾名称模糊查询所有垃圾信息")
    @ApiImplicitParam(name = "garbageName", value = "垃圾名称", required = true, paramType = "query", dataTypeClass = String.class, example = "盒")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/name")
    public CommonResult geAllGarbageListByName(@NotBlank(message = "垃圾名称不能为空")
                                               @Length(message = "垃圾名称长度必须在1~50之间", min = 1, max = 50) String garbageName) {
        List<GarbageVO> garbageVOList = garbageService.geAllGarbageListByName(garbageName);
        return CommonResult.ok(garbageVOList, "通过垃圾名称模糊查询所有垃圾信息成功!");
    }

    @ApiOperation("测试通过垃圾名称、分类名称查询垃圾信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "garbageName", value = "垃圾名称", required = false, paramType = "query", dataTypeClass = String.class, example = "盒"),
            @ApiImplicitParam(name = "categoryName", value = "垃圾分类名称", required = false, paramType = "query", dataTypeClass = String.class, example = "可回收垃圾"),
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
    @GetMapping("/list/name/category/{pageNum}/{pageSize}")
    public CommonResult getGarbageListByNameOrCategory(@Nullable String garbageName,
                                                       @Nullable String categoryName,
                                                       @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                       @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GarbageVO> garbageVOList = garbageService.getGarbageListByNameOrCategory(garbageName, categoryName, pageNum, pageSize);
        return CommonResult.ok(garbageVOList, "通过垃圾名称、分类名称查询垃圾信息成功!");
    }

    @ApiOperation("测试查询垃圾总数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "garbageName", value = "垃圾名称", required = false, paramType = "query", dataTypeClass = String.class, example = "盒"),
            @ApiImplicitParam(name = "categoryName", value = "垃圾分类名称", required = false, paramType = "query", dataTypeClass = String.class, example = "可回收垃圾")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getGarbageCount(@Nullable String garbageName, @Nullable String categoryName) {

        long garbageCount = garbageService.getGarbageCount(garbageName, categoryName);
        return CommonResult.ok(garbageCount, "通过垃圾名称、分类名称获取垃圾总数量成功!");
    }

    @ApiOperation("测试按垃圾种类分组查询垃圾数量")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/total/category")
    public CommonResult getGarbageCountGroupByCategory() {
        List<Map<String, Object>> count = garbageService.getGarbageCountGroupByCategory();
        return CommonResult.ok(count, "按垃圾种类分组查询垃圾数量成功!");

    }

    @ApiOperation("测试修改垃圾信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateGarbage(@Validated({UpdateGroup.class}) @RequestBody Garbage garbage) {
        int result = garbageService.updateGarbage(garbage);
        if (result > 0) {
            return CommonResult.ok("修改垃圾信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改垃圾信息失败!!!");
    }
}
