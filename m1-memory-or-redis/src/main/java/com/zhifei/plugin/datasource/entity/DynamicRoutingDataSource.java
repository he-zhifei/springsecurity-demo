package com.zhifei.plugin.datasource.entity;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * 动态路由数据源
 *
 * @author He Zhifei
 * @date 2020/11/22 21:00
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> DATA_SOURCE = new ThreadLocal<>();

    public static String getDataSource() {
        return DATA_SOURCE.get();
    }

    public static void setDataSource(String dataSource) {
        DATA_SOURCE.set(dataSource);
    }

    public static void clear() {
        DATA_SOURCE.remove();
    }

    public DynamicRoutingDataSource(Map<Object, Object> targetDataSources, Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }
}
