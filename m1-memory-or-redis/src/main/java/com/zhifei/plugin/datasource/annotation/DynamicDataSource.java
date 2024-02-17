package com.zhifei.plugin.datasource.annotation;

import com.zhifei.plugin.datasource.enums.DynamicDataSourceType;

import java.lang.annotation.*;

/**
 * 动态数据源注解，用在service实现方法或者实现类上，不写使用默认的数据源
 *
 * @author He Zhifei
 * @date 2020/12/3 21:00
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DynamicDataSource {
    DynamicDataSourceType value() default DynamicDataSourceType.PRIMARY;
}
