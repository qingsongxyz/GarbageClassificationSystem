package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.pojo.Broadcast;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.BroadcastService;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.BroadcastVO;
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
 * 消息表 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-17
 */
@Api(tags = "消息类测试")
@Validated //非pojo参数检验声明
@RestController
@RequestMapping("/broadcast")
public class BroadcastController {

    private final BroadcastService broadcastService;

    public BroadcastController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @ApiOperation("测试发布消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/a")
    public CommonResult addBroadcast(@Validated({CreateGroup.class}) @RequestBody Broadcast broadcast) {
        int result = broadcastService.addBroadcast(broadcast);
        if (result > 0) {
            return CommonResult.ok("发布消息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "发布消息失败!!!");
    }

    @ApiOperation("测试删除消息")
    @ApiImplicitParam(name = "id", value = "消息id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/{id}")
    public CommonResult deleteBroadcast(@Min(message = "消息id必须大于0", value = 1) @PathVariable("id") long id) {
        int result = broadcastService.deleteBroadcast(id);
        if (result > 0) {
            return CommonResult.ok("删除消息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "删除消息失败!!!");
    }

    @ApiOperation("测试批量删除消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @DeleteMapping("/list")
    public CommonResult deleteBroadcastList(@ValidListPositive(message = "消息id必须都大于0") @RequestBody ValidList<Long> ids) {
        int result = broadcastService.deleteBroadcastList(ids);
        if (result > 0) {
            return CommonResult.ok("批量删除消息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "批量删除消息失败!!!");
    }

    @ApiOperation("测试通过id查询消息")
    @ApiImplicitParam(name = "id", value = "消息id", required = true, paramType = "path", dataTypeClass = Long.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/{id}")
    public CommonResult getBroadcastById(@Min(message = "消息id必须大于0", value = 1) @PathVariable("id") long id) {
        BroadcastVO broadcastVO = broadcastService.getBroadcastById(id);
        return CommonResult.ok(broadcastVO, "通过id查询消息成功!");
    }

    @ApiOperation("测试分页查询消息集合")
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
    public CommonResult getBroadcastList(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                         @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize) {

        List<BroadcastVO> broadcastList = broadcastService.getBroadcastList(pageNum, pageSize);
        return CommonResult.ok(broadcastList, "分页查询消息集合成功!");
    }

    @ApiOperation("测试通过消息标题分页查询信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页面容量", required = true, paramType = "path", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "title", value = "消息标题", required = true, paramType = "query", dataTypeClass = String.class, example = "消息")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/list/title/{pageNum}/{pageSize}")
    public CommonResult getBroadcastListByTitle(@Min(value = 1, message = "页码必须是大于0的数字") @PathVariable("pageNum") int pageNum,
                                                @Min(value = 1, message = "页面容量必须是大于0的数字") @PathVariable("pageSize") int pageSize,
                                                @NotBlank(message = "消息标题不能为空")
                                                @Length(message = "消息标题长度必须在1~50之间", min = 1, max = 50) String title) {
        List<BroadcastVO> broadcastList = broadcastService.getBroadcastListByTitle(title, pageNum, pageSize);
        return CommonResult.ok(broadcastList, "通过消息标题分页查询信息集合成功!");
    }

    @ApiOperation("测试通过消息标题查询所有消息集合")
    @ApiImplicitParam(name = "title", value = "消息标题", required = true, paramType = "query", dataTypeClass = String.class, example = "消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/all/title")
    public CommonResult getAllBroadcastListByTitle(@NotBlank(message = "消息标题不能为空")
                                                   @Length(message = "消息标题长度必须在1~50之间", min = 1, max = 50) String title) {
        List<BroadcastVO> broadcastList = broadcastService.getAllBroadcastListByTitle(title);
        return CommonResult.ok(broadcastList, "通过消息标题查询所有消息集合成功!");
    }

    @ApiOperation("测试查询消息数量")
    @ApiImplicitParam(name = "title", value = "消息标题", required = false, paramType = "query", dataTypeClass = String.class, example = "消息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/count")
    public CommonResult getBroadcastListCount(@Nullable String title) {
        int count = broadcastService.getBroadcastListCount(title);
        return CommonResult.ok(count, "查询消息数量成功!");
    }

    @ApiOperation("测试修改消息信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PutMapping("/{id}")
    public CommonResult updateBroadcast(@Validated({UpdateGroup.class}) @RequestBody Broadcast broadcast) {
        int result = broadcastService.updateBroadcast(broadcast);
        if (result > 0) {
            return CommonResult.ok("修改消息信息成功!");
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "修改消息信息失败!!!");
    }
}
