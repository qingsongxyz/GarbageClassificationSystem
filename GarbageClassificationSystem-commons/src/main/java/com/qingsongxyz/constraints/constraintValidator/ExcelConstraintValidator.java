package com.qingsongxyz.constraints.constraintValidator;

import cn.hutool.core.util.StrUtil;
import com.qingsongxyz.constraints.Excel;
import com.qingsongxyz.constraints.Image;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 图片格式、大小校验
 */
public class ExcelConstraintValidator implements ConstraintValidator<Excel, MultipartFile> {

    private static final List<String> FILE_TYPE = Arrays.asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private static final int ONE_MB = 1024 * 1024;

    private int min;

    private int max;

    @Override
    public void initialize(Excel imageSize) {
        min = imageSize.min();
        max = imageSize.max();
    }

    @Override
public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        String contentType = file.getContentType();
        if(StrUtil.isBlank(contentType) && !FILE_TYPE.contains(contentType)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("上传excel文件格式必须为xlx或者xlsx").addConstraintViolation();
            return false;
        }
        long size = file.getSize();
        if(size == 0 || size < (long) min * ONE_MB || size > (long) max * ONE_MB){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("上传excel文件大小必须在" + min + "MB~" + max + "MB范围内").addConstraintViolation();
            return false;
        }
        return true;
    }
}
