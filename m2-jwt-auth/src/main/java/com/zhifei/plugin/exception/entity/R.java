package com.zhifei.plugin.exception.entity;

import com.zhifei.plugin.exception.enums.RCode;

import java.io.Serializable;

/**
 * 接口返回值包装类，构造器私有化，不允许直接new，可通过类似：
 * R.success().code(xxx).message(xxx).data(xxx) 具体返回哪些属性通过此类的链式调用进行修改
 *
 * @author He Zhifei
 * @date 2020/6/6 13:47
 */
public class R<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应信息，用来说明响应情况
     */
    private String message;

    /**
     * 响应的具体数据
     */
    private T data;

    private R() {
    }

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static R success() {
        return new R(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMessage(), null);
    }

    public static R fail() {
        return new R(RCode.FAILED.getCode(), RCode.FAILED.getMessage(), null);
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> rCode(RCode rCode) {
        this.code = rCode.getCode();
        this.message = rCode.getMessage();
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
