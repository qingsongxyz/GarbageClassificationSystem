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
 * 权限表
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
@TableName("path")
@ApiModel(value = "Path对象", description = "权限表")
public class Path extends Model<Path> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "权限id不能为空", groups = {UpdateGroup.class})
    @Min(message = "权限id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("权限路径")
    @TableField("`path`")
    @NotBlank(message = "权限路径不能为空", groups = {CreateGroup.class})
    @Length(message = "权限路径长度必须在1~255之间", min = 1, max = 255, groups = {CreateGroup.class, UpdateGroup.class})
    private String path;

    @ApiModelProperty("权限描述")
    @TableField("`description`")
    @NotBlank(message = "权限描述不能为空", groups = {CreateGroup.class})
    @Length(message = "权限描述长度必须在1~255之间", min = 1, max = 255, groups = {CreateGroup.class, UpdateGroup.class})
    private String description;

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

    //拥有该权限对应的角色列表
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<RolePathVO> rolePathVOList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
