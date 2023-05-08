package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 垃圾分类表
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("garbage_category")
@ApiModel(value = "GarbageCategory对象", description = "垃圾分类表")
public class GarbageCategory extends Model<GarbageCategory> {

    @ApiModelProperty("垃圾分类主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "垃圾分类id不能为空", groups = {UpdateGroup.class})
    @Min(message = "垃圾分类id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("分类名称")
    @TableField("`name`")
    @NotBlank(message = "垃圾分类名称不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾分类称长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @ApiModelProperty("分类简介")
    @TableField("`description`")
    @NotBlank(message = "垃圾分类简介不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾分类简介长度必须大于1", min = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private String description;

    @ApiModelProperty("分类图片地址")
    @TableField("image")
    @NotBlank(message = "分类图片地址不能为空", groups = {CreateGroup.class})
    @URL(message = "分类图片地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
    private String image;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除字段", hidden = true)
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "乐观锁字段", hidden = true)
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Long version;


    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
