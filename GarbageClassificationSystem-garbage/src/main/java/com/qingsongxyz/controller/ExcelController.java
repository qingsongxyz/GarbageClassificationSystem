package com.qingsongxyz.controller;

import cn.hutool.http.HttpStatus;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.google.common.collect.Lists;
import com.qingsongxyz.config.CustomCellWriteWidthConfig;
import com.qingsongxyz.config.DropDownCellHandler;
import com.qingsongxyz.config.ExcelConfig;
import com.qingsongxyz.constraints.Excel;
import com.qingsongxyz.listen.GarbageImportListener;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.GarbageCategoryService;
import com.qingsongxyz.service.GarbageService;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.vo.GarbageCategoryVO;
import com.qingsongxyz.vo.GarbageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RefreshScope
@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    private final GarbageCategoryService garbageCategoryService;

    private final OSSFeignService ossFeignService;

    private final GarbageService garbageService;

    private final HttpServletResponse response;

    public ExcelController(GarbageCategoryService garbageCategoryService, @Qualifier("oSSFeignService") OSSFeignService ossFeignService, GarbageService garbageService, HttpServletResponse response) {
        this.garbageCategoryService = garbageCategoryService;
        this.ossFeignService = ossFeignService;
        this.garbageService = garbageService;
        this.response = response;
    }

    @ApiOperation("测试excel导入垃圾信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/garbage/upload")
    public CommonResult upload(@Excel MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), GarbageVO.class, new GarbageImportListener(endpoint, bucket, garbageService, garbageCategoryService, ossFeignService))
                .autoCloseStream(Boolean.TRUE)
                .sheet()
                .doRead();
        return CommonResult.ok("导入excel成功!");
    }

    @ApiOperation("测试下载垃圾excel文件")
    @ApiImplicitParam(name = "isTemplate", value = "是否下载模版", required = false, paramType = "query", dataTypeClass = Boolean.class, example = "true")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/garbage/download")
    public void download(@RequestParam(defaultValue = "true") boolean isTemplate) throws IOException {
        String name = "";
        try {
            List<GarbageVO> garbageVOList;
            if (isTemplate) {
                name = "垃圾信息表模版";
                garbageVOList = initGarbage();
            } else {
                name = "垃圾信息表";
                garbageVOList = garbageService.getAllGarbageList();
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            //下拉框数据 所有垃圾分类
            Map<Integer, String[]> dropDownMap = new HashMap<>();
            List<GarbageCategoryVO> garbageCategoryList = garbageCategoryService.getAllGarbageCategoryList();
            String[] categoryArray = garbageCategoryList.stream().map(GarbageCategoryVO::getName).toArray(String[]::new);
            dropDownMap.put(1, categoryArray);

            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), GarbageVO.class)
                    .head(getHeaders())
                    .registerWriteHandler(new HorizontalCellStyleStrategy(ExcelConfig.headConfig(), ExcelConfig.contentConfig()))
                    .registerWriteHandler(new CustomCellWriteWidthConfig())
                    .registerWriteHandler(new DropDownCellHandler(dropDownMap))
                    .autoCloseStream(Boolean.FALSE)
                    .autoTrim(Boolean.TRUE)
                    .sheet(name)
                    .doWrite(garbageVOList);
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
    private List<GarbageVO> initGarbage() {

        ArrayList<GarbageVO> garbageVOList = new ArrayList<>();
        GarbageVO garbageVO = new GarbageVO();
        garbageVO.setName("纸箱");
        garbageVO.setCategory("可回收垃圾");
        garbageVO.setUnit("个");
        garbageVO.setScore(1);
        garbageVO.setImage("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs/category/纸箱.png");
        garbageVOList.add(garbageVO);
        return garbageVOList;
    }

    /**
     * 设置excel头部
     *
     * @return 头部信息
     */
    private List<List<String>> getHeaders() {
        List<List<String>> heads = Lists.newArrayList();
        heads.add(Lists.newArrayList("垃圾名称"));
        heads.add(Lists.newArrayList("垃圾分类"));
        heads.add(Lists.newArrayList("垃圾单位"));
        heads.add(Lists.newArrayList("每单位积分"));
        heads.add(Lists.newArrayList("垃圾图片(网址)"));
        return heads;
    }

}
