package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Good;
import com.qingsongxyz.service.GoodService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GoodVO;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Api(tags = "商品类测试")
@RestController
@Validated //非pojo参数检验声明
@RequestMapping("/good")
public class GoodController {

    private final GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    @ApiOperation("测试添加商品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addGood(@RequestBody @Validated({CreateGroup.class}) Good good) {
        long count = goodService.count(new QueryWrapper<Good>().eq("name", good.getName()));
        if(count > 0){
            return CommonResult.failure(HttpStatus.HTTP_CONFLICT, "商品名称已存在,添加商品失败!!!");
        }
        int result = goodService.addGood(good);
        if(result > 0){
            return CommonResult.ok("添加商品成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "添加商品失败!!!");
    }

    @ApiOperation("测试删除商品")
    @ApiImplicitParam(name = "id", value = "商品id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteGood(@Min(message = "商品id必须为大于0的数字", value = 1)
                                   @PathVariable("id") long id){
        int result = goodService.deleteGood(id);
        if(result > 0){
            return CommonResult.ok("删除商品成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除商品失败!!!");
    }

    @ApiOperation("测试批量删除商品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteGoodList(@ValidListPositive(message = "商品id都必须大于0") @RequestBody ValidList<Long> ids){
        int result = goodService.deleteGoodList(ids);
        if(result > 0){
            return CommonResult.ok("批量删除商品成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除商品失败!!!");
    }

    @ApiOperation("测试分页查询商品集合")
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
    public CommonResult getGoodList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                    @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {
        List<GoodVO> goodVOList = goodService.getGoodList(pageNum, pageSize);
        return CommonResult.ok(goodVOList, "查询商品集合成功!");
    }

    @ApiOperation("测试通过种类名称、商品名称模糊查询商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "商品名称", required = false, paramType = "query", dataTypeClass = String.class, example = "电视机"),
            @ApiImplicitParam(name = "category", value = "商品种类", required = false, paramType = "query", dataTypeClass = String.class, example = "电器"),
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
    public CommonResult getGoodListByNameOrCategory(@Nullable String name, @Nullable String category,
                                                    @Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                    @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize){
        List<GoodVO> goodVOList = goodService.getGoodListByNameOrCategory(name, category, pageNum, pageSize);
        return CommonResult.ok(goodVOList, "通过种类名称、商品名称模糊查询商品集合成功!");
    }

    @ApiOperation("测试通过商品名称模糊查询所有商品集合")
    @ApiImplicitParam(name = "name", value = "商品名称", required = true, paramType = "query", dataTypeClass = String.class, example = "电视机")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/name")
    public CommonResult getAllGoodListByName(@NotBlank(message = "商品名称不能为空")
                                             @Length(message = "商品名称必须在1~50之间", min = 1, max = 50) String name){
        List<GoodVO> goodVOList = goodService.getAllGoodListByName(name);
        return CommonResult.ok(goodVOList, "通过商品名称模糊查询所有商品集合成功!");
    }

    @ApiOperation("测试查询商品总数量")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getAllGoodCount(){
        int count = goodService.getAllGoodCount();
        return CommonResult.ok(count, "查询商品总数量成功!");
    }

    @ApiOperation("测试通过种类名称、商品名称模糊查询商品数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "商品名称", required = false, paramType = "query", dataTypeClass = String.class, example = "电视机"),
            @ApiImplicitParam(name = "category", value = "商品种类", required = false, paramType = "query", dataTypeClass = String.class, example = "电器")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count/name/category")
    public CommonResult getGoodByNameOrCategory(@Nullable String name, @Nullable String category){
        int count = goodService.getGoodByNameOrCategory(name, category);
        return CommonResult.ok(count, "通过种类名称、商品名称模糊查询商品数量成功!");
    }

    @ApiOperation("测试修改商品信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateGood(@RequestBody @Validated({UpdateGroup.class}) Good good){
        int result = goodService.updateGood(good);
        if(result > 0){
            return CommonResult.ok("修改商品信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改商品信息失败!!!");
    }
}

