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
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-17
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("broadcast")
@ApiModel(value = "Broadcast对象", description = "消息表")
public class Broadcast extends Model<Broadcast> {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "消息id不能为空", groups = {UpdateGroup.class})
    @Min(message = "消息id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    @NotNull(message = "用户id不能为空", groups = {CreateGroup.class})
    @Min(message = "用户id必须为大于0的数字", value = 1, groups = {CreateGroup.class})
    private Long userId;

    @ApiModelProperty("消息标题")
    @TableField("title")
    @NotBlank(message = "消息标题不能为空", groups = {CreateGroup.class})
    @Length(message = "消息标题长度必须在1~50之间", min = 1, max = 50, groups = {CreateGroup.class, UpdateGroup.class})
    private String title;

    @ApiModelProperty("消息内容")
    @TableField("content")
    @NotBlank(message = "消息内容不能为空", groups = {CreateGroup.class})
    private String content;

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

    //用户对象
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private UserVO userVO;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
