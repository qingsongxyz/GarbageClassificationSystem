package com.qingsongxyz.config;

import com.alibaba.fastjson.JSON;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MyWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final String realName = ".";

    private final UserServiceImpl userDetailsService;

    public MyWebSecurityConfigurerAdapter(UserServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //将自定义的AuthenticationManager暴露在容器中,使得在其他地方能够注入
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .mvcMatchers("/oauth/**").permitAll()
               .mvcMatchers("/third-party/**").permitAll()
               .anyRequest().authenticated()
               .and()
               .httpBasic()
               .and()
               .exceptionHandling()
               .authenticationEntryPoint((request, response, authException) -> {
                   if(authException instanceof InsufficientAuthenticationException){
                       response.addHeader("WWW-Authenticate", "Basic realm= " + realName);
                       response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                   }else {
                       response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                       response.setHeader("Access-Control-Allow-Origin", "*");
                       response.setHeader("Cache-Control", "no-cache");
                       response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
                       CommonResult result = CommonResult.failure(cn.hutool.http.HttpStatus.HTTP_UNAUTHORIZED, "请先进行认证!");
                       response.getWriter().println(JSON.toJSONString(result));
                   }
                   response.flushBuffer();
               })
               .and()
               .csrf().disable()
               .headers().frameOptions().disable(); //解决页面框架不能获取子页面
    }
}
