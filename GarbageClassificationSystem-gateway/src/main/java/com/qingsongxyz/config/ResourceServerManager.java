package com.qingsongxyz.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.qingsongxyz.util.PathMatchUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;
import com.qingsongxyz.constant.SecurityConstant;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.qingsongxyz.constant.RedisConstant.ROLE_PATH_KEY;

@Configuration
public class ResourceServerManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final PathMatchUtil pathMatchUtil;

    private final StringRedisTemplate stringRedisTemplate;

    public ResourceServerManager(StringRedisTemplate stringRedisTemplate, PathMatchUtil pathMatchUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.pathMatchUtil = pathMatchUtil;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        // 预检请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        String requestPath = request.getURI().getPath();
        // 特定路径跳过token校验
        if (pathMatchUtil.skipValid(requestPath)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 如果token为空 或者token不合法 则进行拦截
        String token = request.getHeaders().getFirst(SecurityConstant.AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, SecurityConstant.JWT_PREFIX)) {
            return Mono.just(new AuthorizationDecision(false));
        }

        //从redis读取角色权限信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(ROLE_PATH_KEY);
        //请求的路径需要的角色信息
        List<String> roleList = new ArrayList<>();
        //是否需要鉴权标志
        boolean require = false;
        PathMatcher pathMatcher = new AntPathMatcher();

        for (Map.Entry<Object, Object> roles : map.entrySet()) {
            String path = String.valueOf(roles.getKey());
            if (pathMatcher.match(path, requestPath)) {
                Object value = roles.getValue();
                List<String> list = JSON.parseArray(value.toString(), String.class);
                roleList.addAll(list);
                require = true;
            }
        }

        //如果路径不需要权限直接放行
        if (!require) {
            return Mono.just(new AuthorizationDecision(true));
        }

        Mono<AuthorizationDecision> authorizationDecisionMono = mono
                .filter(Authentication::isAuthenticated) //过滤出认证过的token
                .flatMapIterable(Authentication::getAuthorities) //获取认证token的权限集合
                .map(GrantedAuthority::getAuthority) //将权限转为字符串
                .any(authority -> {
                    String[] parts = authority.split("_");
                    String role = parts[parts.length - 1];
                    //判断当前角色是否在请求路径的角色集合之中
                    boolean hasAuthorized = CollectionUtil.isNotEmpty(roleList) && roleList.contains(role);
                    return hasAuthorized;
                })
                .map(AuthorizationDecision::new) //创建AuthorizationDecision
                .defaultIfEmpty(new AuthorizationDecision(false)); //当mono对象为null时进行拦截
        return authorizationDecisionMono;
    }
}
