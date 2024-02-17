package com.zhifei.plugin.exception.exception;

import com.zhifei.plugin.exception.enums.RCode;

/**
 * 自定义异常类CustomException
 *
 * @author He Zhifei
 * @date 2020/6/6 13:50
 */
public class CustomException extends RuntimeException {

    private int code;

    private String message;

    /**
     * 使用时，务必使用异常码-异常信息配对使用，方便后续处理
     * @param rCode
     */
    public CustomException(RCode rCode) {
        super(rCode.getMessage());
        this.code = rCode.getCode();
        this.message = rCode.getMessage();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
