package com.zhifei.plugin.datasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhifei.plugin.datasource.entity.DynamicRoutingDataSource;
import com.zhifei.plugin.datasource.enums.DynamicDataSourceType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringBoot没有整合DruidDataSource，因此需要个人配置
 * 需要根据实际情况修改的地方：
 *      basePackages：Mapper接口包路径，支持通配符*和**，前者代表1层路径，后者代表任意层（包括0层）
 *      MAPPER_LOCATIONS：mapper.xml文件路径，支持通配符*和**
 *      TYPE_ALIASES_PACKAGE：允许使用别名的实体类，多个用逗号隔开
 * 如需新增数据源，则类似secondaryDataSource那样新增即可，再在dynamicRoutingDataSource()中添加
 *
 * @author He Zhifei
 * @date 2020/11/22 21:00
 */
@Configuration
@MapperScan(basePackages = "com.zhifei.dao", sqlSessionFactoryRef = "sqlSessionFactory")
public class DynamicDataSourceConfig {

    /**
     * mapper.xml文件路径
     */
    public static final String MAPPER_LOCATIONS = "mapper/**/*.xml";

    /**
     * 该包下的实体类，在mybatis的xml中可以使用别名
     */
    public static final String TYPE_ALIASES_PACKAGE = "com.zhifei.entity";

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return new DruidDataSource();
    }

    /**
     * 动态数据源
     * @return
     */
    @Bean
    @Primary
    public DynamicRoutingDataSource dynamicRoutingDataSource(
            @Autowired DataSource primaryDataSource,
            @Autowired DataSource secondaryDataSource
    ) {
        // 需要动态切换的多个数据源
        Map<Object, Object> targetDataSources = new HashMap(2);
        targetDataSources.put(DynamicDataSourceType.PRIMARY.name(), primaryDataSource);
        targetDataSources.put(DynamicDataSourceType.SECONDARY.name(), secondaryDataSource);

        // 指定不使用@DynamicDataSource注解指定数据源时的默认数据源primaryDataSource
        return new DynamicRoutingDataSource(targetDataSources, primaryDataSource);
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager(@Autowired DynamicRoutingDataSource dynamicRoutingDataSource) {
        return new DataSourceTransactionManager(dynamicRoutingDataSource);
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Autowired DynamicRoutingDataSource dynamicRoutingDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicRoutingDataSource);
        sessionFactory.setTypeAliasesPackage(DynamicDataSourceConfig.TYPE_ALIASES_PACKAGE);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DynamicDataSourceConfig.MAPPER_LOCATIONS));

        // 因为使用的是druid，所以需要手动配置Mybatis拦截器，默认显示SQL执行时间
//        LogInterceptor logInterceptor = new LogInterceptor();
//        logInterceptor.setProperties(new Properties() {{put(LogInterceptor.SHOW_EXECUTION_TIME, false);}});
//        sessionFactory.setPlugins(logInterceptor);

        return sessionFactory.getObject();
    }
}
