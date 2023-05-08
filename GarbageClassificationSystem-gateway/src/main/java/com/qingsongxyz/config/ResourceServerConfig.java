package com.qingsongxyz.config;

import cn.hutool.json.JSONUtil;
import com.qingsongxyz.pojo.CommonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import com.qingsongxyz.constant.SecurityConstant;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Value("${KeyPair.password}")
    private String password;

    private final ResourceServerManager resourceServerManager;

    public ResourceServerConfig(ResourceServerManager resourceServerManager) {
        this.resourceServerManager = resourceServerManager;
    }

    /**
     * 跨域配置
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(Duration.ofDays(30));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(SecurityConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(SecurityConstant.JWT_AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * token无效或者已过期自定义响应
     */
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                        response.getHeaders().set("Access-Control-Allow-Origin", "*");
                        response.getHeaders().set("Cache-Control", "no-cache");
                        String body = JSONUtil.toJsonStr(CommonResult.failure(cn.hutool.http.HttpStatus.HTTP_UNAUTHORIZED, "token非法或失效!!!"));
                        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                        return response.writeWith(Mono.just(buffer))
                                .doOnError(error -> DataBufferUtils.release(buffer));
                    });
            return mono;
        };
    }

    /**
     * 没有权限自定义响应
     */
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, e) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> {
                        response.setStatusCode(HttpStatus.FORBIDDEN);
                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                        response.getHeaders().set("Access-Control-Allow-Origin", "*");
                        response.getHeaders().set("Cache-Control", "no-cache");
                        String body = JSONUtil.toJsonStr(CommonResult.failure(cn.hutool.http.HttpStatus.HTTP_FORBIDDEN, "没有权限!!!"));
                        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                        return response.writeWith(Mono.just(buffer))
                                .doOnError(error -> DataBufferUtils.release(buffer));
                    });
            return mono;
        };
    }

    public RSAPublicKey publicKey() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("gcs.jks"), password.toCharArray());
        KeyPair keyPair = factory.getKeyPair("gcs", password.toCharArray());
        return (RSAPublicKey) keyPair.getPublic();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer()
                .authenticationEntryPoint(authenticationEntryPoint())
                .jwt()
                .publicKey(publicKey())
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        http.authorizeExchange()
                .anyExchange().access(resourceServerManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()) // 处理未授权
                .authenticationEntryPoint(authenticationEntryPoint()) //处理未认证
                .and()
                .cors()
                .configurationSource(corsConfigurationSource()) //允许跨域
                .and()
                .csrf().disable()
                .headers().frameOptions().disable(); //解决页面框架不能获取子页面
        return http.build();
    }
}
