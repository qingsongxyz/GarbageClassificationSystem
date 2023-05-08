package com.qingsongxyz.util;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import java.util.ArrayList;
import java.util.List;

@RefreshScope
@Setter
@Component("pathMatchUtil")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "security")
public class PathMatchUtil {

    private List<String> ignoreUrls;


    /**
     * 跳过token校验
     * @param path 请求路径
     * @return 是否跳过检验
     */
    public boolean skipValid(String path) {
        for (String ignoreUrl : ignoreUrls) {
            if (StrUtil.isBlank(ignoreUrl) || StrUtil.isBlank(path)) {
                return false;
            }
            PathMatcher matcher = new AntPathMatcher();
            boolean flag = matcher.match(ignoreUrl, path);
            if(flag){
                return true;
            }
        }
        return false;
    }

    /**
     * 检验是否为token相关字段
     * @param path 请求路径
     * @return 是否为token相关字段
     */
    public boolean isTokenPath(String path){
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add("/api/gcs-auth/oauth/token");
        pathList.add("/api/gcs-auth/oauth/check_token");
        for (String url : pathList) {
            PathMatcher matcher = new AntPathMatcher();
            boolean flag = matcher.match(url, path);
            if(flag){
                return true;
            }
        }
        return false;
    }

    /**
     * 检验是否为WebSocket连接路径
     * @param path 请求路径
     * @return 是否为WebSocket连接路径
     */
    public boolean isWebSocketPath(String path){
        PathMatcher matcher = new AntPathMatcher();
        return matcher.match("/api/gcs-admin/ws/**", path);
    }
}
