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
 * 购物车详情表
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-16
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("market_details")
@ApiModel(value = "MarketDetails对象", description = "购物车详情表")
public class MarketDetails extends Model<MarketDetails> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("购物车id")
    @TableField("market_id")
    @NotNull(message = "购物车id不能为空", groups = {CreateGroup.class})
    @Min(message = "购物车id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long marketId;

    @ApiModelProperty("商品id")
    @TableField("good_id")
    @NotNull(message = "商品id不能为空", groups = {CreateGroup.class})
    @Min(message = "商品id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long goodId;

    @ApiModelProperty("商品名称")
    @TableField("good_name")
    @NotBlank(message = "商品名称不能为空", groups = {CreateGroup.class})
    @Length(message = "商品名称长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String goodName;

    @ApiModelProperty("商品图片")
    @TableField("good_image")
    @NotBlank(message = "商品图片地址不能为空", groups = {CreateGroup.class})
    @URL(message = "商品图片地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
    private String goodImage;

    @ApiModelProperty("商品所需积分")
    @TableField("good_score")
    @NotNull(message = "商品所需积分不能为空", groups = {CreateGroup.class})
    @Min(message = "商品所需积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer goodScore;

    @ApiModelProperty("商品数量")
    @TableField("count")
    @NotNull(message = "商品数量不能为空", groups = {CreateGroup.class})
    @Min(message = "商品数量必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer count;

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
