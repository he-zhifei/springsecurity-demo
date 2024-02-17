package com.zhifei.plugin.exception.enums;

/**
 * 异常码（只需新增异常信息，抛出自定义的通用异常即可）
 *
 * @author He Zhifei
 * @date 2020/6/6 13:45
 */
public enum RCode {

    /**
     * 操作成功
     */
    SUCCESS(10000, "操作成功"),

    /**
     * 操作失败
     */
    FAILED(-10000, "操作失败"),

    /**
     * 参数校验失败
     */
    VALIDATE_FAILED(-10001, "参数校验失败"),

    /**
     * 缺失请求体
     */
    MISSING_REQUEST_BODY(-10002, "缺失请求体数据"),

    /**
     * 请求体数据格式有误
     */
    REQUEST_BODY_DATA_ERROR(-10003, "请求体数据格式有误"),

    /**
     * 请先登录
     */
    NEED_LOGIN(-20000, "请先登录"),

    /**
     * 用户名不能为空
     */
    BLANK_PRINCIPAL(-20001, "用户名不能为空"),

    /**
     * 密码不能为空
     */
    BLANK_CREDENTIAL(-20002, "密码不能为空"),

    /**
     * 用户名不正确
     */
    INCORRECT_PRINCIPAL(-20003, "用户名不正确"),

    /**
     * 密码不正确
     */
    INCORRECT_CREDENTIAL(-20004, "密码不正确"),

    /**
     * 权限不足
     */
    UNAUTHORIZED(-20005, "权限不足"),

    /**
     * 账号已被冻结
     */
    ACCOUNT_LOCKED(-20006, "账号已被冻结"),

    /**
     * 未知错误
     */
    ERROR(-50000, "未知错误");

    private int code;

    private String message;

    RCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}