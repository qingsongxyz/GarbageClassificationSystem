package com.qingsongxyz.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.ArrayList;

@Configuration
public class SwaggerConfig {

    private final Environment environment;

    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnClass({OpenApiAutoConfiguration.class})
    public Docket createRestApi() {

        //获取系统环境,只用dev、test环境才开启Swagger
        Profiles profiles = Profiles.of("dev", "test");
        boolean flag = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.OAS_30)
                .groupName("开发1组")
                .select()
                //.paths(PathSelectors.ant("/list/**")) //访问路径过滤
                .apis(RequestHandlerSelectors.basePackage("com.qingsongxyz.controller")) //包过滤
                .build()
                .apiInfo(createApiInfo())
                .enable(flag);
    }

    @Bean
    public ApiInfo createApiInfo() {
        return new ApiInfo("qingsongxyz Swagger",
                "qingsongxyz Api Documentation",
                "3.0",
                "http:127.0.0.1",
                new Contact("qingsongxyz", "127.0.0.1", "3608802405@qq.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }

}
