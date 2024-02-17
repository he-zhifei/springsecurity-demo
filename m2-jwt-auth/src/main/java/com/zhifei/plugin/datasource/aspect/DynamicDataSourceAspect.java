package com.zhifei.plugin.datasource.aspect;

import com.zhifei.plugin.datasource.annotation.DynamicDataSource;
import com.zhifei.plugin.datasource.entity.DynamicRoutingDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 动态数据源切面
 *
 * @author He Zhifei
 * @date 2020/11/22 21:00
 */
@Component
@Aspect
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.zhifei.plugin.datasource.annotation.DynamicDataSource) || @within(com.zhifei.plugin.datasource.annotation.DynamicDataSource)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DynamicDataSource dynamicDataSource = null;
            Signature signature = joinPoint.getSignature();
            if (signature instanceof MethodSignature) {
                dynamicDataSource = ((MethodSignature) signature).getMethod().getAnnotation(DynamicDataSource.class);
            }
            if (dynamicDataSource == null) {
                dynamicDataSource = joinPoint.getTarget().getClass().getAnnotation(DynamicDataSource.class);
            }
            if (dynamicDataSource != null) {
                DynamicRoutingDataSource.setDataSource(dynamicDataSource.value().name());
            }
            return joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            DynamicRoutingDataSource.clear();
        }
    }
}
