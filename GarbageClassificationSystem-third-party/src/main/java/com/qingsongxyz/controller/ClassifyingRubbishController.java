package com.qingsongxyz.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpStatus;
import com.aliyun.imagerecog20190930.Client;
import com.aliyun.imagerecog20190930.models.ClassifyingRubbishRequest;
import com.aliyun.imagerecog20190930.models.ClassifyingRubbishResponse;
import com.aliyun.imagerecog20190930.models.ClassifyingRubbishResponseBody;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.qingsongxyz.constraints.Image;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.util.UrlImageUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.InputStream;

/**
 * <p>
 * 垃圾分类识别前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@RefreshScope
@Api(tags = "垃圾分类识别测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/classify-rubbish")
public class ClassifyingRubbishController {

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKey;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    private OSS ossClient;

    @ApiOperation("测试垃圾分类图像识别(file)")
    @ApiImplicitParam(name = "file", value = "图片文件", required = true, paramType = "form", dataTypeClass = MultipartFile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/recognize/image")
    public CommonResult recognizeImage(@Image MultipartFile file) throws Exception {

        //1.创建客户端
        Config config = new Config()
                .setAccessKeyId(accessId)
                .setAccessKeySecret(accessKey);

        config.endpoint = "imagerecog.cn-shanghai.aliyuncs.com";

        Client client = new Client(config);

        //2.创建垃圾分类请求和参数
        InputStream inputStream = file.getInputStream();
        ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        String fileName = IdUtil.simpleUUID() + ".png";
        String filePath = "gcs/classify/" + fileName;
        ossClient.putObject(bucket, filePath, inputStream);
        ossClient.shutdown();

        ClassifyingRubbishRequest request = new ClassifyingRubbishRequest();

        //3.设置图片流
        String ossUrl = "https://" + bucket + "." + endpoint + "/" + filePath;
        request.setImageURL(ossUrl);

        try {
            //4.发起请求 返回响应信息
            ClassifyingRubbishResponse response = client.classifyingRubbish(request);
            ClassifyingRubbishResponseBody responseBody = response.getBody();
            ClassifyingRubbishResponseBody.ClassifyingRubbishResponseBodyData data = responseBody.getData();
            return CommonResult.ok(data, "垃圾分类图像识别成功!");
        } catch (TeaException e) {
            log.error("ClassifyingRubbishController TeaException:{}", e.getMessage());
            Common.assertAsString(e.getMessage());
        } catch (Exception _e) {
            log.error("ClassifyingRubbishController Exception:{}", _e.getMessage());
            TeaException teaException = new TeaException(_e.getMessage(), _e);
            Common.assertAsString(teaException);
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "垃圾分类图像识别失败!!!");
    }

    @ApiOperation("测试垃圾分类图像识别(url)")
    @ApiImplicitParam(name = "url", value = "图片url", required = true, paramType = "query", dataTypeClass = String.class, example = "http://baidu.com/1.png")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/recognize/url")
    public CommonResult recognizeUrl(@NotBlank(message = "图片url不能为空")
                                     @URL(message = "用户头像地址必须为正确的url") String url) throws Exception {
        //1.创建客户端
        Config config = new Config()
                .setAccessKeyId(accessId)
                .setAccessKeySecret(accessKey);

        config.endpoint = "imagerecog.cn-shanghai.aliyuncs.com";

        Client client = new Client(config);

        //2.创建垃圾分类请求和参数
        ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        InputStream inputStream = UrlImageUtil.getUrlImage(url);
        String fileName = IdUtil.simpleUUID() + ".png";
        String filePath = "gcs/classify/" + fileName;
        ossClient.putObject(bucket, filePath, inputStream);
        ossClient.shutdown();

        ClassifyingRubbishRequest request = new ClassifyingRubbishRequest();

        //3.设置图片url
        String ossUrl = "https://" + bucket + "." + endpoint + "/" + filePath;
        request.setImageURL(ossUrl);

        try {
            //4.发起请求 返回响应信息
            ClassifyingRubbishResponse response = client.classifyingRubbish(request);
            ClassifyingRubbishResponseBody responseBody = response.getBody();
            ClassifyingRubbishResponseBody.ClassifyingRubbishResponseBodyData data = responseBody.getData();
            return CommonResult.ok(data, "垃圾分类图像识别成功!");
        } catch (TeaException e) {
            log.error("ClassifyingRubbishController TeaException:{}", e.getMessage());
            Common.assertAsString(e.getMessage());
        } catch (Exception _e) {
            log.error("ClassifyingRubbishController Exception:{}", _e.getMessage());
            TeaException teaException = new TeaException(_e.getMessage(), _e);
            Common.assertAsString(teaException);
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "垃圾分类图像识别失败!!!");
    }
}
