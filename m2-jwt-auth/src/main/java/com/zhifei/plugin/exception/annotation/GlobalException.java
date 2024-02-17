package com.zhifei.plugin.exception.annotation;

import com.zhifei.plugin.exception.entity.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解标记的Controller，将进行异常的增强处理，把异常信息封装到 {@link R} 里
 *
 * @author He Zhifei
 * @date 2020/6/6 13:30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalException {
}
