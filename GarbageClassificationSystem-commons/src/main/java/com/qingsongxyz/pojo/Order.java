package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.OrderDetailsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("`order`")
@ApiModel(value = "Order对象", description = "订单表")
public class Order extends Model<Order> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    @NotNull(message = "用户id不能为空", groups = {CreateGroup.class})
    @Min(message = "用户id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long userId;

    @ApiModelProperty("订单所需总积分")
    @TableField("sum")
    @NotNull(message = "订单所需总积分不能为空", groups = {CreateGroup.class})
    @Min(message = "订单所需总积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer sum;

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

    //订单中的详情信息
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @NotNull(message = "订单详情信息不能为空", groups = {CreateGroup.class})
    @Size(min = 1, message = "订单详情信息不能为空", groups = {CreateGroup.class})
    @Valid
    private List<OrderDetails> orderDetailsList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
