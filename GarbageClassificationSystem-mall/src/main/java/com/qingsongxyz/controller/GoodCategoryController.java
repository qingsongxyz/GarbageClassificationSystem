package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Good;
import com.qingsongxyz.pojo.GoodCategory;
import com.qingsongxyz.service.GoodCategoryService;
import com.qingsongxyz.service.GoodService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GoodCategoryVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品种类表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Api(tags = "商品种类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/good-category")
public class GoodCategoryController {

    private final GoodCategoryService goodCategoryService;

    private final GoodService goodService;

    public GoodCategoryController(GoodCategoryService goodCategoryService, GoodService goodService) {
        this.goodCategoryService = goodCategoryService;
        this.goodService = goodService;
    }

    @ApiOperation("测试添加商品种类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGoodCategory(@RequestBody @Validated({CreateGroup.class}) GoodCategory goodCategory) {
        long count = goodCategoryService.count(new QueryWrapper<GoodCategory>().eq("category", goodCategory.getCategory()));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "商品种类名称已存在,添加商品种类失败!!!");
        }
        int result = goodCategoryService.addGoodCategory(goodCategory);
        if (result > 0) {
            return CommonResult.ok("添加商品种类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加商品种类失败!!!");
    }

    @ApiOperation("测试删除商品种类")
    @ApiImplicitParam(name = "id", value = "商品种类id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGoodCategory(@Min(message = "商品种类id必须为大于0的数字", value = 1)
                                           @PathVariable("id") long id) {
        long count = goodService.count(new QueryWrapper<Good>().eq("category_id", id).eq("deleted", 0));
        if (count > 0) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该种类的商品,删除商品种类失败!!!");
        }
        int result = goodCategoryService.deleteGoodCategory(id);
        if (result > 0) {
            return CommonResult.ok("删除商品种类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除商品种类失败!!!");
    }

    @ApiOperation("测试批量删除商品种类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteGoodCategoryList(@ValidListPositive(message = "商品种类id都必须大于0") @RequestBody ValidList<Long> ids) {
        boolean flag = true;
        List<Long> deletedList = new ArrayList<>();
        for (Long id : ids) {
            long count = goodService.count(new QueryWrapper<Good>().eq("category_id", id).eq("deleted", 0));
            if (count > 0) {
                flag = false;
                continue;
            }
            deletedList.add(id);
        }
        int result = goodCategoryService.deleteGoodCategoryList(deletedList);
        if (!flag) {
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "存在该种类的商品,批量删除商品种类失败!!!");
        }
        if (result > 0) {
            return CommonResult.ok("批量删除商品种类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除商品种类失败!!!");
    }

    @ApiOperation("测试分页查询商品种类集合")
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
    public CommonResult getGoodCategoryList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                            @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GoodCategoryVO> goodCategoryVOList = goodCategoryService.getGoodCategoryList(pageNum, pageSize);
        return CommonResult.ok(goodCategoryVOList, "查询商品种类集合成功!");
    }

    @ApiOperation("测试通过种类名称模糊查询商品种类集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "商品种类名称", required = true, paramType = "query", dataTypeClass = String.class, example = "电器"),
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
    @GetMapping("/list/category/{pageNum}/{pageSize}")
    public CommonResult getGoodCategoryListByCategory(@NotBlank(message = "商品种类不能为空")
                                                      @Length(message = "商品种类长度必须在1~50之间", min = 1, max = 50) String category,
                                                      @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                      @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GoodCategoryVO> goodCategoryVOList = goodCategoryService.getGoodCategoryListByCategory(category, pageNum, pageSize);
        return CommonResult.ok(goodCategoryVOList, "通过种类名称模糊查询商品种类集合成功!");
    }

    @ApiOperation("测试通过种类名称模糊查询所有商品种类集合")
    @ApiImplicitParam(name = "category", value = "商品种类名称", required = true, paramType = "query", dataTypeClass = String.class, example = "电器")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/category")
    public CommonResult getAllGoodCategoryListByCategory(@NotBlank(message = "商品种类不能为空")
                                                         @Length(message = "商品种类长度必须在1~50之间", min = 1, max = 50) String category) {
        List<GoodCategoryVO> goodCategoryVOList = goodCategoryService.getAllGoodCategoryListByCategory(category);
        return CommonResult.ok(goodCategoryVOList, "通过种类名称模糊查询所有商品种类集合成功!");
    }

    @ApiOperation("测试查询所有商品种类集合")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list")
    public CommonResult getAllGoodCategoryList() {
        List<GoodCategoryVO> goodCategoryVOList = goodCategoryService.getAllGoodCategoryList();
        return CommonResult.ok(goodCategoryVOList, "查询所有商品种类集合成功!");
    }

    @ApiOperation("测试查询商品种类总数")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllGoodCategoryCount() {
        int count = goodCategoryService.getAllGoodCategoryCount();
        return CommonResult.ok(count, "查询商品种类总数成功!");
    }

    @ApiOperation("测试通过种类名称模糊查询商品种类数量")
    @ApiImplicitParam(name = "category", value = "商品种类名称", required = true, paramType = "query", dataTypeClass = String.class, example = "电器")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/category")
    public CommonResult getGoodCategoryCountByCategory(@NotBlank(message = "商品种类不能为空")
                                                       @Length(message = "商品种类长度必须在1~50之间", min = 1, max = 50) String category) {
        int count = goodCategoryService.getGoodCategoryCountByCategory(category);
        return CommonResult.ok(count, "通过种类名称模糊查询商品种类数量成功!");
    }

    @ApiOperation("测试修改商品种类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateGoodCategory(@RequestBody @Validated({UpdateGroup.class}) GoodCategory goodCategory) {
        int result = goodCategoryService.updateGoodCategory(goodCategory);
        if (result > 0) {
            return CommonResult.ok("修改商品种类成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改商品种类失败!!!");
    }
}
