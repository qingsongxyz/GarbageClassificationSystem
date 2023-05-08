package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * 垃圾信息表
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
@TableName("garbage")
@ApiModel(value = "Garbage对象", description = "垃圾信息表")
public class Garbage extends Model<Garbage> {

    @ApiModelProperty("垃圾表主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "垃圾id不能为空", groups = {UpdateGroup.class})
    @Min(message = "垃圾id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("垃圾名称")
    @TableField("`name`")
    @NotBlank(message = "垃圾名称不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾名称长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @ApiModelProperty("垃圾分类id")
    @TableField("category_id")
    @NotNull(message = "垃圾分类id不能为空", groups = {CreateGroup.class})
    @Min(message = "垃圾分类id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long categoryId;

    @ApiModelProperty("垃圾单位")
    @TableField("unit")
    @NotBlank(message = "垃圾单位不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾单位长度必须在1~10之间", min = 1, max = 10, groups = {CreateGroup.class, UpdateGroup.class})
    private String unit;

    @ApiModelProperty("每一个单位的积分")
    @TableField("score")
    @NotNull(message = "单位积分不能为空", groups = {CreateGroup.class})
    @Min(message = "单位积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer score;

    @ApiModelProperty("垃圾图片地址")
    @TableField("image")
    @NotBlank(message = "垃圾图片地址不能为空", groups = {CreateGroup.class})
    @URL(message = "垃圾图片地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
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

    //垃圾分类对象
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private GarbageCategory garbageCategory;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
