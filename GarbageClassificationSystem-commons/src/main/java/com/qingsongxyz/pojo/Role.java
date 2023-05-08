package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.RolePathVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色表
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
@TableName("role")
@ApiModel(value = "Role对象", description = "角色表")
public class Role extends Model<Role> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "角色id不能为空", groups = {UpdateGroup.class})
    @Min(message = "角色id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("角色英文名")
    @TableField("`role`")
    @NotBlank(message = "角色英文名不能为空", groups = {CreateGroup.class})
    @Length(message = "角色英文名长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String role;

    @ApiModelProperty("角色中文名")
    @TableField("`name`")
    @NotBlank(message = "角色中文名不能为空", groups = {CreateGroup.class})
    @Length(message = "角色中文名长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

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

    //角色拥有的权限列表
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private List<RolePathVO> rolePathVOList;

    @Override
    public Serializable pkVal() {
        return this.name;
    }

}
