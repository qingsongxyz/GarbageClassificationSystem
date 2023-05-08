package com.qingsongxyz.config;

import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RefreshScope
@Configuration
@EnableAuthorizationServer
public class MyJwtAuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Value("${KeyPair.password}")
    private String password;

    private final DataSource dataSource;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserServiceImpl userDetailsService;

    public MyJwtAuthorizationServer(DataSource dataSource, AuthenticationManager authenticationManagerBean, PasswordEncoder passwordEncoder, UserServiceImpl userDetailsService) {
        this.dataSource = dataSource;
        this.authenticationManager = authenticationManagerBean;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ClientDetailsService jdbcClientDetailsService(){
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("gcs.jks"), password.toCharArray());
        KeyPair keyPair = factory.getKeyPair("gcs", password.toCharArray());
        return keyPair;
    }

    /**
     * 设置token密钥
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            HashMap<String, Object> map = new HashMap<>();
            if (authentication instanceof UserDetails) {
                UserDTO user = (UserDTO) authentication.getUserAuthentication();
                map.put("id", user.getId());
                map.put("username", user.getUsername());
                map.put("roleList", user.getRoleList());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            }
            return accessToken;
        };
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        ArrayList<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true); //支持令牌刷新
        tokenServices.setReuseRefreshToken(false); //不重复使用刷新令牌
        tokenServices.setClientDetailsService(jdbcClientDetailsService());
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        //令牌过期时间7天
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7));
        //刷新令牌有效性10天(以秒为单位)
        tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(10));
        return tokenServices;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(){
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 配置端点信息
     *
     * @param endpoints 端点
     * @throws Exception 异常
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //token增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        ArrayList<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        List<TokenGranter> tokenGranters = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));

        tokenGranters.add(new MyTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        //创建组合检验器 注入
        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);

        endpoints.authenticationManager(authenticationManager) //认证管理器
                .authorizationCodeServices(authorizationCodeServices()) //授权码服务
                .userDetailsService(userDetailsService) //刷新令牌时会用到
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenGranter(compositeTokenGranter)
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain); //设置token增强
    }

    /**
     * 配置客户端信息
     *
     * @param clients 客户端
     * @throws Exception 异常
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetailsService());
    }

    /**
     * 配置服务端安全信息
     *
     * @param security 服务端安全信息
     * @throws Exception 异常
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()") //公开 /oauth/token_key端点
                .checkTokenAccess("permitAll()"); //公开 /oauth/check_token端点
    }
}
