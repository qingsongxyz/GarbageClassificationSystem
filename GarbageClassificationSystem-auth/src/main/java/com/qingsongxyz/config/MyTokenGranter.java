package com.qingsongxyz.config;

import com.alibaba.fastjson.JSON;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.vo.RoleVO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 自定义授权模式(用于手机号短信验证码登录和三方扫码登录)
 */
public class MyTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "my";

    protected MyTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        OAuth2AccessToken token = super.grant(grantType, tokenRequest);
        if (token != null) {
           token = new DefaultOAuth2AccessToken(token);
        }
        return token;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String user = parameters.get("user");
        UserSerializableDTO userSerializableDTO = JSON.parseObject(user, UserSerializableDTO.class);

        //存储用户权限集合
        Set<GrantedAuthority> set = new HashSet<>();
        for (RoleVO role : userSerializableDTO.getRoleList()) {
            set.add(new SimpleGrantedAuthority(SecurityConstant.AUTHORITY_PREFIX + role.getRole()));
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, set);
        OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, authentication);
    }
}
