package com.example.springboot.common;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
public class Result implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    public static Result error(String message) {
        return new Result(500, message, null);
    }

    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
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