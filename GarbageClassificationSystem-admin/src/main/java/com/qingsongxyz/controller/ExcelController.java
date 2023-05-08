package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.qingsongxyz.config.CustomCellWriteWidthConfig;
import com.qingsongxyz.config.DropDownCellHandler;
import com.qingsongxyz.config.ExcelConfig;
import com.qingsongxyz.constraints.Excel;
import com.qingsongxyz.pojo.UserTemplate;
import com.qingsongxyz.listen.UserImportListener;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.vo.GarbageCategoryVO;
import com.qingsongxyz.vo.UserVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RefreshScope
@Api(tags = "Excel导入导出测试")
@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    private final UserService userService;

    private final RoleService roleService;

    private final HttpServletResponse response;

    private final OSSFeignService ossFeignService;

    public ExcelController(UserService userService, RoleService roleService, HttpServletResponse response, @Qualifier("oSSFeignService") OSSFeignService ossFeignService) {
        this.userService = userService;
        this.roleService = roleService;
        this.response = response;
        this.ossFeignService = ossFeignService;
    }

    @ApiOperation("测试excel导入用户信息")
    @ApiImplicitParam(name = "file", value = "excel文件", required = true, paramType = "query", dataTypeClass = MultipartFile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/user/upload")
    public CommonResult upload(@Excel MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), UserTemplate.class, new UserImportListener(endpoint, bucket, userService, roleService, ossFeignService))
                .autoCloseStream(Boolean.TRUE)
                .sheet()
                .doRead();
        return CommonResult.ok("导入excel成功!");
    }

    @ApiOperation("测试下载用户excel文件")
    @ApiImplicitParam(name = "isTemplate", value = "是否下载模版", required = false, paramType = "query", dataTypeClass = Boolean.class, example = "true")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/user/download")
    public void download(@Nullable @RequestParam(defaultValue = "true") boolean isTemplate) throws IOException {
        String name = "";
        try {
            List<UserVO> userVOList;
            if (isTemplate) {
                name = "用户信息表模版";
                userVOList = initUser();
            } else {
                name = "用户信息表";
                userVOList = userService.getAllUsersByName("");
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 这里需要设置不关闭流
            if (isTemplate) {
                //下拉框数据 性别分类
                Map<Integer, String[]> dropDownMap = new HashMap<>();
                String[] genderArray = {"男", "女"};
                dropDownMap.put(2, genderArray);

                EasyExcel.write(response.getOutputStream(), UserTemplate.class)
                        .head(getTemplateHeaders())
                        .registerWriteHandler(new HorizontalCellStyleStrategy(ExcelConfig.headConfig(), ExcelConfig.contentConfig()))
                        .registerWriteHandler(new CustomCellWriteWidthConfig())
                        .registerWriteHandler(new DropDownCellHandler(dropDownMap))
                        .autoCloseStream(Boolean.FALSE)
                        .autoTrim(Boolean.TRUE)
                        .sheet(name)
                        .doWrite(userVOList);
            } else {
                EasyExcel.write(response.getOutputStream(), UserVO.class)
                        .head(getHeaders())
                        .registerWriteHandler(new HorizontalCellStyleStrategy(ExcelConfig.headConfig(), ExcelConfig.contentConfig()))
                        .registerWriteHandler(new CustomCellWriteWidthConfig())
                        .autoCloseStream(Boolean.FALSE)
                        .autoTrim(Boolean.TRUE)
                        .sheet(name)
                        .doWrite(userVOList);
            }
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            CommonResult commonResult = CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "下载" + name + "失败!!!");
            response.getWriter().println(JSON.toJSONString(commonResult));
        }
    }

    /**
     * 模版样例数据
     */
    private List<UserVO> initUser() {
        ArrayList<UserVO> userVOList = new ArrayList<>();
        UserVO userVO = new UserVO();
        userVO.setUsername("张三");
        userVO.setGender("男");
        userVO.setAge(18);
        userVO.setImage("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs/user.png");
        userVOList.add(userVO);
        return userVOList;
    }

    /**
     * 设置模版excel头部
     *
     * @return 模版头部信息
     */
    private List<List<String>> getTemplateHeaders() {
        List<List<String>> heads = Lists.newArrayList();
        heads.add(Lists.newArrayList("用户名"));
        heads.add(Lists.newArrayList("年龄"));
        heads.add(Lists.newArrayList("性别"));
        heads.add(Lists.newArrayList("头像(网址)"));
        return heads;
    }

    /**
     * 设置excel头部
     *
     * @return 头部信息
     */
    private List<List<String>> getHeaders() {
        List<List<String>> heads = Lists.newArrayList();
        heads.add(Lists.newArrayList("用户名"));
        heads.add(Lists.newArrayList("角色"));
        heads.add(Lists.newArrayList("年龄"));
        heads.add(Lists.newArrayList("性别"));
        heads.add(Lists.newArrayList("个性签名"));
        heads.add(Lists.newArrayList("头像(网址)"));
        heads.add(Lists.newArrayList("邮箱"));
        heads.add(Lists.newArrayList("手机号"));
        heads.add(Lists.newArrayList("积分"));
        heads.add(Lists.newArrayList("是否锁定"));
        return heads;
    }
}
