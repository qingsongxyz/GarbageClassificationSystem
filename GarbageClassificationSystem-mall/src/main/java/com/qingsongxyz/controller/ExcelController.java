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
import com.qingsongxyz.excel.GoodTemplate;
import com.qingsongxyz.listener.GoodImportListener;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.GoodCategoryService;
import com.qingsongxyz.service.GoodService;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.vo.GoodCategoryVO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RefreshScope
@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    private final GoodService goodService;

    private final GoodCategoryService goodCategoryService;

    private final HttpServletResponse response;

    private final OSSFeignService ossFeignService;

    public ExcelController(GoodService goodService, GoodCategoryService goodCategoryService, HttpServletResponse response, @Qualifier("oSSFeignService") OSSFeignService ossFeignService) {
        this.goodService = goodService;
        this.goodCategoryService = goodCategoryService;
        this.response = response;
        this.ossFeignService = ossFeignService;
    }

    @ApiOperation("测试excel导入商品信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/good/upload")
    public CommonResult upload(@Excel MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), GoodTemplate.class, new GoodImportListener(endpoint, bucket, goodService, goodCategoryService, ossFeignService))
                .autoCloseStream(Boolean.TRUE)
                .sheet()
                .doRead();
        return CommonResult.ok("导入excel成功!");
    }

    @ApiOperation("测试下载商品excel文件")
    @ApiImplicitParam(name = "isTemplate", value = "是否下载模版", required = false, paramType = "query", dataTypeClass = Boolean.class, example = "true")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/good/download")
    public void download(@RequestParam(value = "isTemplate", required = false, defaultValue = "true") boolean isTemplate) throws IOException {
        String name = "";
        try {
            List<GoodTemplate> goodTemplateList = null;
            if (isTemplate) {
                name = "商品信息表模版";
                goodTemplateList = initGood();
            } else {
                name = "商品信息表";
                goodTemplateList = goodService.getAllGoodList();
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 这里需要设置不关闭流
            if (isTemplate) {
                //下拉框数据 商品种类
                Map<Integer, String[]> dropDownMap = new HashMap<>();
                List<GoodCategoryVO> goodCategoryList = goodCategoryService.getAllGoodCategoryList();
                String[] categoryArray = goodCategoryList.stream().map(GoodCategoryVO::getCategory).toArray(String[]::new);
                dropDownMap.put(1, categoryArray);

                EasyExcel.write(response.getOutputStream())
                        .head(getTemplateHeaders())
                        .registerWriteHandler(new HorizontalCellStyleStrategy(ExcelConfig.headConfig(), ExcelConfig.contentConfig()))
                        .registerWriteHandler(new CustomCellWriteWidthConfig())
                        .registerWriteHandler(new DropDownCellHandler(dropDownMap))
                        .autoCloseStream(Boolean.FALSE)
                        .autoTrim(Boolean.TRUE)
                        .sheet(name)
                        .doWrite(goodTemplateList);
            } else {
                EasyExcel.write(response.getOutputStream())
                        .head(getHeaders())
                        .registerWriteHandler(new HorizontalCellStyleStrategy(ExcelConfig.headConfig(), ExcelConfig.contentConfig()))
                        .registerWriteHandler(new CustomCellWriteWidthConfig())
                        .autoCloseStream(Boolean.FALSE)
                        .autoTrim(Boolean.TRUE)
                        .sheet(name)
                        .doWrite(goodTemplateList);
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
    private List<GoodTemplate> initGood() {
        ArrayList<GoodTemplate> goodTemplateList = new ArrayList<>();
        GoodTemplate good = new GoodTemplate();
        good.setName("电视机");
        good.setCategory("电器");
        good.setImage("https://img0.baidu.com/it/u=4287774991,3978879937&fm=253&fmt=auto&app=138&f=JPEG?w=741&h=500");
        good.setScore(1);
        good.setStorage(1);
        goodTemplateList.add(good);
        return goodTemplateList;
    }

    /**
     * 设置模版excel头部
     *
     * @return 模版头部信息
     */
    private List<List<String>> getTemplateHeaders() {
        List<List<String>> heads = Lists.newArrayList();
        heads.add(Lists.newArrayList("商品名称"));
        heads.add(Lists.newArrayList("商品种类"));
        heads.add(Lists.newArrayList("商品所需积分"));
        heads.add(Lists.newArrayList("商品库存"));
        heads.add(Lists.newArrayList("商品图片(网址)"));
        return heads;
    }

    /**
     * 设置excel头部
     *
     * @return 头部信息
     */
    private List<List<String>> getHeaders() {
        List<List<String>> heads = Lists.newArrayList();
        heads.add(Lists.newArrayList("商品名称"));
        heads.add(Lists.newArrayList("商品种类"));
        heads.add(Lists.newArrayList("商品所需积分"));
        heads.add(Lists.newArrayList("商品库存"));
        heads.add(Lists.newArrayList("商品图片(网址)"));
        return heads;
    }
}
