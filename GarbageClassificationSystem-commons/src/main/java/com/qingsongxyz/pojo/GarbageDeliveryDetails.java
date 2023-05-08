package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.aspectj.lang.annotation.Around;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 垃圾投递详情表
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("garbage_delivery_details")
@ApiModel(value = "GarbageDeliveryDetails对象", description = "垃圾投递详情表")
public class GarbageDeliveryDetails extends Model<GarbageDeliveryDetails> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "垃圾投递详情id不能为空", groups = {UpdateGroup.class})
    @Min(message = "垃圾投递详情id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("垃圾投递id")
    @TableField("delivery_id")
    @Min(message = "垃圾投递id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long deliveryId;

    @ApiModelProperty("垃圾名称")
    @TableField("garbage_name")
    @NotBlank(message = "垃圾名称不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾名称长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String garbageName;

    @ApiModelProperty("垃圾图片")
    @TableField("garbage_image")
    @NotBlank(message = "垃圾图片地址不能为空", groups = {CreateGroup.class})
    @URL(message = "垃圾图片地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
    private String garbageImage;

    @ApiModelProperty("垃圾分类")
    @TableField("garbage_category")
    @NotBlank(message = "垃圾分类名称不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾分类称长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String garbageCategory;

    @ApiModelProperty("垃圾单位")
    @TableField("garbage_unit")
    @NotBlank(message = "垃圾单位不能为空", groups = {CreateGroup.class})
    @Length(message = "垃圾单位长度必须在1~10之间", min = 1, max = 10, groups = {CreateGroup.class, UpdateGroup.class})
    private String garbageUnit;

    @ApiModelProperty("垃圾每单位积分")
    @TableField("garbage_score")
    @NotNull(message = "单位积分不能为空", groups = {CreateGroup.class})
    @Min(message = "单位积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer garbageScore;

    @ApiModelProperty("垃圾单位总量")
    @TableField("count")
    @NotNull(message = "垃圾单位总量不能为空", groups = {CreateGroup.class})
    @Min(message = "垃圾单位总量必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer count;

    @ApiModelProperty("垃圾总积分")
    @TableField("sum")
    @NotNull(message = "垃圾总积分不能为空", groups = {CreateGroup.class})
    private Integer sum;

    @ApiModelProperty("投递垃圾是否分类正确")
    @TableField("flag")
    @NotNull(message = "投递垃圾是否分类正确字段不能为空", groups = {CreateGroup.class})
    @Min(message = "投递垃圾是否分类正确字段只能为0或1", value = 0, groups = {CreateGroup.class, UpdateGroup.class})
    @Max(message = "投递垃圾是否分类正确字段只能为0或1", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer flag;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除字段")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("乐观锁字段")
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Long version;


    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
