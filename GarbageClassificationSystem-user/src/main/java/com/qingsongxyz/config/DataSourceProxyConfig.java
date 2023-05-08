package com.qingsongxyz.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.qingsongxyz.config.mybatisplus.SnowFlakeIdGenerator;
import com.qingsongxyz.handler.MyMetaObjectHandler;
import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({MybatisPlusProperties.class})
public class DataSourceProxyConfig {

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;

    private final MybatisPlusInterceptor mybatisPlusInterceptor;

    private final MyMetaObjectHandler myMetaObjectHandler;

    private final SnowFlakeIdGenerator snowFlakeIdGenerator;

    private final MybatisPlusProperties properties;

    public DataSourceProxyConfig(MybatisPlusProperties properties, MybatisPlusInterceptor mybatisPlusInterceptor, MyMetaObjectHandler myMetaObjectHandler, SnowFlakeIdGenerator snowFlakeIdGenerator) {
        this.properties = properties;
        this.mybatisPlusInterceptor = mybatisPlusInterceptor;
        this.myMetaObjectHandler = myMetaObjectHandler;
        this.snowFlakeIdGenerator = snowFlakeIdGenerator;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Primary //@Primary标识必须配置在数据源上，否则本地事务失效
    @Bean
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSourceProxy);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if(configuration == null){
            configuration = new MybatisConfiguration();
        }
        sqlSessionFactoryBean.setConfiguration(configuration);

        // 向代理数据源添加插件
        sqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor);

        // 设置全局配置,增加自动填充主键、插入更新字段
        sqlSessionFactoryBean.setGlobalConfig(new GlobalConfig()
                .setMetaObjectHandler(myMetaObjectHandler)
                .setIdentifierGenerator(snowFlakeIdGenerator));

        return sqlSessionFactoryBean;
    }
}
