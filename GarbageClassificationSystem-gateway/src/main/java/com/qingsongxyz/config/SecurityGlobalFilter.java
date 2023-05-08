package com.qingsongxyz.config;

import cn.hutool.core.util.StrUtil;
import com.nimbusds.jose.JWSObject;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.util.PathMatchUtil;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Setter
public class SecurityGlobalFilter implements GlobalFilter, Ordered {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    private final PathMatchUtil pathMatchUtil;

    public SecurityGlobalFilter(PathMatchUtil pathMatchUtil) {
        this.pathMatchUtil = pathMatchUtil;
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(SecurityConstant.AUTHORIZATION_KEY);
        String requestPath = request.getURI().getPath();

        //对于websocket连接移除跨域请求头 ACCESS_CONTROL_ALLOW_ORIGIN
        if(pathMatchUtil.isWebSocketPath(requestPath)){
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        }

        //如果不包含Authorization请求头或Authorization请求头不是Bearer格式的
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, SecurityConstant.JWT_PREFIX)) {
            //对于token相关的请求且没有Basic格式请求头的 添加Basic格式请求头
            if (pathMatchUtil.isTokenPath(requestPath) && !StrUtil.startWithIgnoreCase(token, SecurityConstant.BASIC_PREFIX)) {
                String baseStr = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
                request = exchange.getRequest().mutate()
                        .header(SecurityConstant.AUTHORIZATION_KEY, SecurityConstant.BASIC_PREFIX + baseStr)
                        .build();
                exchange = exchange.mutate().request(request).build();
            }
            return chain.filter(exchange);
        }

        //去除Bearer头
        token = StrUtil.replaceIgnoreCase(token, SecurityConstant.JWT_PREFIX, "");
        //解析JWT中的payload 放入请求头中
        String payload = StrUtil.toString(JWSObject.parse(token).getPayload());
        request = exchange.getRequest().mutate()
                .header(SecurityConstant.JWT_PAYLOAD_KEY, URLEncoder.encode(payload, "UTF-8"))
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
