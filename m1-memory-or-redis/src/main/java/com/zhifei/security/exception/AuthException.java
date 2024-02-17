package com.zhifei.security.exception;

import com.zhifei.plugin.exception.enums.RCode;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证异常，认证流程统一使用这个异常，方便处理异常信息，要求为
 * {@link AuthenticationException}的子类，否则在认证失败处理器中无法捕获此异常。
 *
 * @author He Zhifei
 * @date 2023/10/16 12:22
 */
public class AuthException extends AuthenticationServiceException {

    private int code;

    private String message;

    public AuthException(RCode rCode) {
        super(rCode.getMessage());
        this.code = rCode.getCode();
        this.message = rCode.getMessage();
    }

    public AuthException(String message) {
        super(message);
        this.code = RCode.FAILED.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
