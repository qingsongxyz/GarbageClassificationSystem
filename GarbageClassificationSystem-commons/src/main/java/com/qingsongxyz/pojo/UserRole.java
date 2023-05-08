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
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("user_role")
@ApiModel(value = "UserRole对象", description = "用户角色表")
public class UserRole extends Model<UserRole> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    @NotNull(message = "用户id不能为空", groups = {CreateGroup.class})
    @Min(message = "用户id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long userId;

    @ApiModelProperty("角色id")
    @TableField("role_id")
    @NotNull(message = "角色id不能为空", groups = {CreateGroup.class})
    @Min(message = "角色id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long roleId;

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
