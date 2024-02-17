package com.zhifei.plugin.exception.advice;

import com.google.common.collect.ImmutableMap;
import com.zhifei.plugin.exception.entity.R;
import com.zhifei.plugin.exception.enums.RCode;
import com.zhifei.plugin.exception.exception.CustomException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 统一处理controller层的异常，这个是基类，方便后续扩展，也可以直接在当前类配置 {@link RestControllerAdvice} 注解，但需要增加异常码时，
 * 需要修改当前源码，不方便扩展。
 *
 * 请求参数校验（校验结果统一到exceptionHandler方法处理）：
 * 1.Content-Type为application/json参数校验，pojo校验，pojo参数上加上@RequestBody @Valid，并在pojo需要校验的属性下加上具体校验的注解
 * 2.Content-Type为application/x-www-form-urlencode参数绑定，pojo校验，pojo参数上加上@Valid，并在pojo需要校验的属性下加上具体校验的注解
 * 3.单个参数校验，用法：需要在Controller类上加上@Validated，并在参数上加上@NotEmpty之类的注解
 * 4.pojo校验分组（比如在此场景中新增需要密码，更新用户一般信息时不支持密码修改）：
 *      因此，这里共两个组，分别为新建的Insert（这里只有用户密码为Insert组）和默认的Default小组（不指定group属性的小组），在新增的接口pojo参数上使用
 * @Validated({Default.class, Insert.class}) 就包括了所有要校验的属性。而在更新时，只需要使用@Validated，也就是指校验默认小组的属性，这样就能达到
 * 新增时校验密码，而修改时不校验密码的效果了。
 * 5.在校验pojo情况下：@Valid 等效于 @Validated 等效于@Validated(Default.class)
 *
 * 总结：
 *      把当前文件夹拷贝到SpringBoot项目中，并且在当前类的注解上指定basePackages或者annotations（需要在Controller使用该注解），
 * 在Controller类上使用@Validated进行单个请求参数校验，在具体方法的形参上使用@Validated，必要时进行pojo属性分组校验。同一个pojo属性
 * 的不同校验注解分组一般要保持一致，比如User.sex属性的@Pattern和@NotNull的分组都为Insert.class
 * @Pattern好像只能用于匹配String类型？？？？？
 *
 * @author He Zhifei
 * @date 2020/6/6 13:30
 */
public abstract class AbstractControllerExceptionAdvice {

    /**
     * 定义map，配置异常类型所对应的错误代码
     */
    private static ImmutableMap<Class<? extends Throwable>, RCode> exceptionsMap;

    /**
     * 定义map的builder对象，去构建ImmutableMap
     */
    protected static ImmutableMap.Builder<Class<? extends Throwable>, RCode> builder = ImmutableMap.builder();

    /**
     * Controller层的所有异常处理
     * @param e 异常类
     * @return 异常信息
     */
    @ExceptionHandler(Exception.class)
    public R<String> exceptionHandler(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            /**
             * Content-Type为application/json参数校验异常
             */
            // 从异常对象中拿到ObjectError对象
            ObjectError error = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0);
            // 然后提取错误提示信息进行返回
            return R.fail().code(RCode.VALIDATE_FAILED.getCode()).message(error.getDefaultMessage());
        } else if (e instanceof BindException) {
            /**
             * Content-Type为application/x-www-form-urlencode参数绑定异常
             */
            ObjectError error = ((BindException) e).getBindingResult().getAllErrors().get(0);
            return R.fail().code(RCode.VALIDATE_FAILED.getCode()).message(error.getDefaultMessage());
        } else if (e instanceof ConstraintViolationException) {
            /**
             * 单个参数校验
             */
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) e).getConstraintViolations();
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                return R.fail().code(RCode.VALIDATE_FAILED.getCode()).message(constraintViolation.getMessage());
            }
        } else if (e instanceof HttpMessageNotReadableException) {
            if (e.getMessage().contains("Required request body is missing")) {
                return R.fail().rCode(RCode.MISSING_REQUEST_BODY);
            }
        } else if (e instanceof CustomException) {
            /**
             * 自定义{@link CustomException}异常处理
             */
            CustomException ce = (CustomException) e;
            return R.fail().code(ce.getCode()).message(ce.getMessage());
        }

        if (exceptionsMap == null) {
            exceptionsMap = builder.build();
        }
        RCode rCode = exceptionsMap.get(e.getClass());
        if (rCode != null) {
            return R.fail().rCode(rCode);
        } else {
            /**
             * 其它异常统一处理
             */
            return R.fail().code(RCode.ERROR.getCode()).message(e.getMessage());
        }
    }

}
