package com.qingsongxyz.dto;

import com.qingsongxyz.validation.BindingGroup;
import com.qingsongxyz.validation.RegisterGroup;
import com.qingsongxyz.vo.RoleVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSerializableDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空", groups = {BindingGroup.class, RegisterGroup.class})
    private String username;

    @NotBlank(message = "密码不能为空", groups = {BindingGroup.class})
    private String password;

    @NotBlank(message = "用户名不能为空", groups = {BindingGroup.class, RegisterGroup.class})
    private String image;

    private String openid;

    private String alipayid;

    private Integer accountLocked;

    private Integer deleted;

    private List<RoleVO> roleList;
}
