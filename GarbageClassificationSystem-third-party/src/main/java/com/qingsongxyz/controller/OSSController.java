package com.qingsongxyz.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpStatus;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.qingsongxyz.constraints.Image;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.util.UrlImageUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * OSS 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@RefreshScope
@Api(tags = "OSS测试")
@RestController
@Validated
@Slf4j
@RequestMapping("/oss")
public class OSSController {

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKey;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    private OSS ossClient;

    @ApiOperation("测试获取OSS信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/policy")
    public Map<String, String> policy() {
        // 填写Host地址，格式为https://bucketname.endpoint。
        String host = "https://" + bucket + "." + endpoint;
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
        String dir = "gcs/";

        ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> map = new LinkedHashMap<>();
            map.put("accessId", accessId);
            map.put("policy", encodedPolicy);
            map.put("signature", postSignature);
            map.put("dir", dir);
            map.put("host", host);
            ossClient.shutdown();
            return map;

        } catch (Exception e) {
            log.info("OSSController policy error:{}", e.getMessage());
        }
        return null;
    }

    @ApiOperation("测试通过url下载网络图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imageName", value = "图片名称", required = true, paramType = "query", dataTypeClass = String.class, example = "纸箱"),
            @ApiImplicitParam(name = "url", value = "图片url", required = true, paramType = "query", dataTypeClass = String.class, example = "http://baidu.com/1.png"),
            @ApiImplicitParam(name = "type", value = "图片类型", required = true, paramType = "query", dataTypeClass = String.class, example = "good")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/url")
    public CommonResult downloadUrlImage(@NotBlank(message = "图片名称不能为空") String imageName,
                                         @NotBlank(message = "图片url不能为空")
                                         @URL(message = "图片地址必须为正确的url") String url,
                                         @NotBlank(message = "图片类型不能为空") String type) {
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
            InputStream inputStream = UrlImageUtil.getUrlImage(url);
            String filePath = "gcs/" + type + "/" + imageName + ".png";
            ossClient.putObject(bucket, filePath, inputStream);
            ossClient.shutdown();
            return CommonResult.ok("下载网络图片成功!");
        } catch (IOException e) {
            log.info("OSSController downloadUrlImage error:{}", e.getMessage());
        }
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "下载网络图片失败!!!");
    }

    @ApiOperation("测试上传图片到阿里云")
    @ApiImplicitParam(name = "file", value = "文件", required = true, paramType = "form", dataTypeClass = MultipartFile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @PostMapping("/file")
    public Map<String, Object> uploadFile(@Image MultipartFile file){
        HashMap<String, Object> map = new HashMap<>();
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
            InputStream inputStream = file.getInputStream();
            String id = IdUtil.fastSimpleUUID();
            String filePath = "gcs/broadcast/" + id + ".png";
            ossClient.putObject(bucket, filePath, inputStream);
            ossClient.shutdown();

            String src = "https://" + bucket + "." + endpoint + "/" + filePath;
            HashMap<String, Object> data = new HashMap<>();
            data.put("url", src);
            data.put("alt", file.getOriginalFilename() + ".png");
            data.put("src", src);

            map.put("errno", 0);
            map.put("data", data);
        } catch (IOException e) {
            log.info("OSSController uploadFile error:{}", e.getMessage());
            map.put("errno", 1);
            map.put("message", e.getMessage());
        }
        return map;
    }
}
