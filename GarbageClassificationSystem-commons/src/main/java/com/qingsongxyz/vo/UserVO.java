package com.qingsongxyz.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.converter.RoleListStringConverter;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserVO extends Model<UserVO> {

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "用户名")
    private String username;

    @ExcelProperty(value = "角色", converter = RoleListStringConverter.class)
    private List<RoleVO> roleList;

    @ExcelProperty(value = "年龄", converter = IntegerStringConverter.class)
    private Integer age;

    @ExcelProperty(value = "性别")
    private String gender;

    @ExcelProperty(value = "个性签名")
    private String signature;

    @ExcelProperty(value = "头像")
    private String image;

    @ExcelProperty(value = "邮箱")
    private String email;

    @ExcelProperty(value = "手机号")
    private String phone;

    @ExcelProperty(value = "积分", converter = LongStringConverter.class)
    private Long score;

    @ExcelProperty(value = "是否锁定", converter = IntegerStringConverter.class)
    private Integer accountLocked;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
