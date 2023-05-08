package com.qingsongxyz.config;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.fastjson.JSON;
import com.qingsongxyz.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义 Sentinel 限流提示
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SentinelConfig {

    @Bean
    public UrlBlockHandler urlBlockHandler(){
        return (request, response, e) -> {
            String requestURI = request.getRequestURI();
            log.info("资源:{}被限流...", requestURI);
            CommonResult result = CommonResult.failure(HttpStatus.HTTP_UNAVAILABLE, "服务器不可用, 请稍后再试!!!");
            String msg = JSON.toJSONString(result);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(msg);
        };
    }
}
