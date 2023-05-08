package com.qingsongxyz.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.ExcelGroup;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GarbageVO extends Model<GarbageVO> {

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "垃圾名称")
    @NotBlank(message = "垃圾名称不能为空", groups = {ExcelGroup.class})
    @Length(message = "垃圾名称长度必须在1~50之间", min = 1, max = 50, groups = {ExcelGroup.class})
    private String name;

    @ExcelProperty(value = "垃圾分类")
    @NotBlank(message = "垃圾分类名称不能为空", groups = {ExcelGroup.class})
    @Length(message = "垃圾分类称长度必须在1~50之间", min = 1, max = 50, groups = {ExcelGroup.class})
    private String category;

    @ExcelProperty(value = "垃圾单位")
    @NotBlank(message = "垃圾单位不能为空", groups = {ExcelGroup.class})
    @Length(message = "垃圾单位长度必须在1~10之间", min = 1, max = 10, groups = {ExcelGroup.class})
    private String unit;

    @ExcelProperty(value = "每单位积分")
    @NotNull(message = "单位积分不能为空", groups = {ExcelGroup.class})
    @Min(message = "单位积分必须为大于0的数字", value = 1, groups = {ExcelGroup.class})
    private Integer score;

    @ExcelProperty(value = "垃圾图片(网址)")
    @NotBlank(message = "垃圾图片地址不能为空", groups = {ExcelGroup.class})
    @URL(message = "垃圾图片地址必须为正确的url", groups = {ExcelGroup.class})
    private String image;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
