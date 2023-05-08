package com.qingsongxyz.dto;

import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.vo.RoleVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private String image;

    private String openid;

    private String alipayid;

    private Integer accountLocked;

    private Integer deleted;

    private List<RoleVO> roleList;

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
