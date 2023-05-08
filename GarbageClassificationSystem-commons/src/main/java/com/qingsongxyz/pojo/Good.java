package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.GoodCategoryVO;
import com.qingsongxyz.vo.StorageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品表
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
@TableName("good")
@ApiModel(value = "Good对象", description = "商品表")
public class Good extends Model<Good> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "商品id不能为空", groups = {UpdateGroup.class})
    @Min(message = "商品id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("商品名称")
    @TableField("`name`")
    @NotBlank(message = "商品名称不能为空", groups = {CreateGroup.class})
    @Length(message = "商品名称必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @ApiModelProperty("商品种类id")
    @TableField("category_id")
    @Min(message = "商品种类id必须为大于0的数字", value = 1, groups = {CreateGroup.class})
    private Long categoryId;

    @ApiModelProperty("商品图片")
    @TableField("image")
    @NotBlank(message = "商品图片不能为空", groups = {CreateGroup.class})
    @URL(message = "商品图片地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
    private String image;

    @ApiModelProperty("商品所需积分")
    @TableField("score")
    @NotNull(message = "商品所需积分不能为空", groups = {CreateGroup.class})
    @Min(message = "商品所需积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer score;

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

    //商品种类
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private GoodCategoryVO goodCategoryVO;

    //商品库存
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @Valid
    private Storage storage;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
