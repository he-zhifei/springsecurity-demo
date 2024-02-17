package com.zhifei.plugin.exception.advice;

import com.zhifei.plugin.exception.annotation.GlobalException;
import com.zhifei.plugin.exception.enums.RCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一处理controller层的异常实现类：
 * basePackages-包级别，指定哪个包下的类需要增强
 * annotations-类级别，需要在该类上使用{@link GlobalException}
 *
 * @author He Zhifei
 * @date 2020/8/24 19:53
 */
@RestControllerAdvice(/*basePackages = {"com.zhifei.controller"}, */
        annotations = {GlobalException.class})
public class GlobalControllerExceptionAdvice extends AbstractControllerExceptionAdvice {

    // 方便其他系统（或配置指定异常对应的异常码）使用，异常与返回结果描述信息对应
    static {
        // SpringSecurity鉴权
        builder.put(AccessDeniedException.class, RCode.UNAUTHORIZED);
    }
}
