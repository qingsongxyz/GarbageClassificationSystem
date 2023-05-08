package com.qingsongxyz.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.qingsongxyz.validation.ExcelGroup;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 用户excel模版对象
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTemplate {

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空", groups = {ExcelGroup.class})
    @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20, groups = {ExcelGroup.class})
    private String username;

    @ExcelProperty(value = "年龄")
    @Min(message = "年龄必须为大于0的数字", value = 1, groups = {ExcelGroup.class})
    @Max(message = "年龄必须为小于120的数字", value = 120, groups = {ExcelGroup.class})
    private Integer age;

    @ExcelProperty(value = "性别")
    @Length(message = "性别长度必须为1", min = 1, max = 1, groups = {ExcelGroup.class})
    private String gender;

    @ExcelProperty(value = "头像(网址)")
    @URL(message = "用户头像地址必须为正确的url", groups = {ExcelGroup.class})
    private String image;
}
