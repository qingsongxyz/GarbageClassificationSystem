package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.PathVO;
import com.qingsongxyz.vo.RoleVO;
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
 * 角色权限表
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("role_path")
@ApiModel(value = "RolePath对象", description = "角色权限表")
public class RolePath extends Model<RolePath> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "角色权限id不能为空", groups = {UpdateGroup.class})
    @Min(message = "角色权限id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("角色id")
    @TableField("role_id")
    @Min(message = "角色id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long roleId;

    @ApiModelProperty("权限id")
    @TableField("path_id")
    @NotNull(message = "权限id不能为空", groups = {CreateGroup.class})
    @Min(message = "权限id必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Long pathId;

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

    //对应的角色信息
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private RoleVO roleVO;

    //对应的权限信息
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private PathVO pathVO;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
