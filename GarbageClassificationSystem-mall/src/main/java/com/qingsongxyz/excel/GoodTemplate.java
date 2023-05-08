package com.qingsongxyz.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 商品excel模板对象
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GoodTemplate {

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "商品名称")
    private String name;

    @ExcelProperty(value = "商品种类")
    private String category;

    @ExcelProperty(value = "商品所需积分")
    private Integer score;

    @ExcelProperty(value = "商品库存")
    private Integer storage;

    @ExcelProperty(value = "商品图片(网址)")
    private String image;
}
