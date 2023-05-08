package com.qingsongxyz.constraints.constraintValidator;

import com.qingsongxyz.constraints.Province;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 校验省份是否合法
 */
public class ProvinceConstraintValidator implements ConstraintValidator<Province, String> {

    private static final List<String> DEFAULT_PROVINCE = Arrays.asList("安徽", "福建", "广东", "广西", "贵州",
            "甘肃", "海南", "河南", "黑龙江", "湖北",
            "湖南", "河北", "江苏", "江西", "吉林",
            "辽宁", "宁夏", "内蒙古", "青海", "山东",
            "山西", "陕西", "四川", "台湾", "西藏",
            "新疆", "云南", "浙江", "其他");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return DEFAULT_PROVINCE.contains(value);
    }
}
