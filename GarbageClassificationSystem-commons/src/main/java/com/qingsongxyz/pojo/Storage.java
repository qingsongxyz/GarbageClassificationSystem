package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存表
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("storage")
@ApiModel(value = "Storage对象", description = "库存表")
public class Storage extends Model<Storage> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("商品id")
    @TableField("good_id")
    @Min(message = "商品id必须为大于0的数字", value = 1, groups = {CreateGroup.class})
    private Long goodId;

    @ApiModelProperty("商品库存")
    @TableField("`storage`")
    @NotNull(message = "商品库存不能为空", groups = {CreateGroup.class})
    @Min(message = "商品库存必须为非负整数", value = 0, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer storage;

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
