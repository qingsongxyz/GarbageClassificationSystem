package com.qingsongxyz.constraints.constraintValidator;

import cn.hutool.core.util.StrUtil;
import com.qingsongxyz.constraints.Image;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 图片格式、大小校验
 */
public class ImageConstraintValidator implements ConstraintValidator<Image, MultipartFile> {

    private static final List<String> FILE_TYPE = Arrays.asList("image/png", "image/jpg", "image/jpeg");

    private static final int ONE_MB = 1024 * 1024;

    private int min;

    private int max;

    @Override
    public void initialize(Image imageSize) {
        min = imageSize.min();
        max = imageSize.max();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        String contentType = file.getContentType();
        if(StrUtil.isBlank(contentType) && !FILE_TYPE.contains(contentType)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("上传图片格式必须为png、jpg、jpeg").addConstraintViolation();
            return false;
        }
        long size = file.getSize();
        if(size == 0 || size < (long) min * ONE_MB || size > (long) max * ONE_MB){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("上传图片大小必须在" + min + "MB~" + max + "MB范围内").addConstraintViolation();
            return false;
        }
        return true;
    }
}
