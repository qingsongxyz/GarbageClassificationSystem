package com.qingsongxyz.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import com.qingsongxyz.vo.RoleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表
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
@TableName("user")
@ApiModel(value = "User对象", description = "用户表")
public class User extends Model<User> implements UserDetails {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @NotNull(message = "用户id不能为空", groups = {UpdateGroup.class})
    @Min(message = "用户id必须为大于0的数字", value = 1, groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("用户名")
    @TableField("username")
    @NotBlank(message = "用户名不能为空", groups = {CreateGroup.class})
    @Length(message = "用户名长度必须在6~20之间", min = 6, max = 20, groups = {CreateGroup.class, UpdateGroup.class})
    @Pattern(regexp = "^\\w+$", message = "用户名只能包含数字、英文字符和下划线", groups = {CreateGroup.class})
    private String username;

    @ApiModelProperty("密码")
    @TableField("`password`")
    @NotBlank(message = "密码不能为空", groups = {CreateGroup.class})
    @Length(message = "密码长度必须在6~30之间", min = 6, max = 30, groups = {CreateGroup.class, UpdateGroup.class})
    private String password;

    @ApiModelProperty("年龄")
    @TableField("age")
    @Min(message = "年龄必须为大于0的数字", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    @Max(message = "年龄必须为小于120的数字", value = 120, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer age;

    @ApiModelProperty("性别")
    @TableField("gender")
    @Length(message = "性别长度必须为1", min = 1, max = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private String gender;

    @ApiModelProperty("个性签名")
    @TableField("signature")
    @Length(message = "个性签名长度必须大于1", min = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private String signature;

    @ApiModelProperty("用户头像")
    @TableField("image")
    @URL(message = "用户头像地址必须为正确的url", groups = {CreateGroup.class, UpdateGroup.class})
    private String image;

    @ApiModelProperty("邮箱")
    @TableField("email")
    @Pattern(message = "邮箱必须为正确的格式", regexp = "^\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}$", groups = {CreateGroup.class, UpdateGroup.class})
    private String email;

    @ApiModelProperty("用户对应微信的唯一标识")
    @TableField("openid")
    private String openid;

    @ApiModelProperty("用户对应支付宝的唯一标识")
    @TableField("alipayid")
    private String alipayid;

    @ApiModelProperty("手机号")
    @TableField("phone")
    @Pattern(message = "手机号必须为正确的格式", regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,2-9]))\\d{8}$", groups = {CreateGroup.class, UpdateGroup.class})
    private String phone;

    @ApiModelProperty("积分")
    @TableField("score")
    @Min(message = "积分必须为非负整数", value = 0, groups = {CreateGroup.class, UpdateGroup.class})
    private Long score;

    @ApiModelProperty("用户是否被锁定")
    @TableField("account_locked")
    @Min(message = "用户是否被锁定的值必须在0~1之间", value = 0, groups = {CreateGroup.class, UpdateGroup.class})
    @Max(message = "用户是否被锁定的值必须在0~1之间", value = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer accountLocked;

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

    //角色集合
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private List<RoleVO> roleList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        roleList.forEach(role -> {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(SecurityConstant.AUTHORITY_PREFIX + role.getRole());
            authorities.add(simpleGrantedAuthority);
        });
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLocked == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return deleted == 0;
    }
}
