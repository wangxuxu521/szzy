package com.example.springboot.util;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static Result success() {
        return success("操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     */
    public static Result success(String message) {
        return new Result(200, message);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     * @param data    返回数据
     */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }

    /**
     * 失败返回结果
     */
    public static Result error() {
        return error("操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static Result error(String message) {
        return new Result(500, message);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 提示信息
     */
    public static Result error(Integer code, String message) {
        return new Result(code, message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
} 