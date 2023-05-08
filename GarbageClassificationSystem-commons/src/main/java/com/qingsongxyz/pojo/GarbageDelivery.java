package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 垃圾投递表
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
@TableName("garbage_delivery")
@ApiModel(value = "GarbageDelivery对象", description = "垃圾投递表")
public class GarbageDelivery extends Model<GarbageDelivery> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "垃圾投递id不能为空", groups = {UpdateGroup.class})
    @Min(message = "垃圾投递id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    @NotNull(message = "用户id不能为空", groups = {CreateGroup.class})
    @Min(message = "用户id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long userId;

    @ApiModelProperty("投递总积分")
    @TableField("total")
    @Min(message = "投递总积分必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer total;

    @ApiModelProperty("投放垃圾站地址")
    @TableField("station_address")
    @NotBlank(message = "投放垃圾站地址不能为空", groups = {CreateGroup.class})
    private String stationAddress;

    @ApiModelProperty("投递状态(0-预发放,1-正常, 2-错误, 3-异常)")
    @TableField("status")
    @Min(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 0, groups = {CreateGroup.class, UpdateGroup.class})
    @Max(message = "投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)", value = 3, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer status;

    @ApiModelProperty("附加信息")
    @TableField("addition")
    private String addition;

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

    //用户信息
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private UserVO userVO;

    //投递详情信息集合
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @NotNull(message = "投递详情信息不能为空", groups = {CreateGroup.class})
    @Size(min = 1, message = "投递详情信息不能为空", groups = {CreateGroup.class})
    @Valid
    private List<GarbageDeliveryDetails> garbageDeliveryDetailsList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
