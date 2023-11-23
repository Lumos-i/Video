package com.video.utils;


//接口响应公共类


public class BaseResponse<T> {
    //响应状态码
    private static final int CODE_SUCCESS = 200;
    private static final int NULL_VALUE = 204;
    private static final int CODE_ERROR = 500;
    private static final int ERROR_LOGIN = 401;
    private static final int NO_POWER = 403;
    private static final int ERROR_WEB = 404;

    //相关属性
    private int code;

    private String msg;

    private T data;
    //构造器
    public BaseResponse(int code, String msg, T data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }
    //相应的响应方法
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<T>(CODE_SUCCESS, message, null);
    }
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(CODE_SUCCESS, "success", data);
    }
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<T>(CODE_SUCCESS, message, data);
    }


    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<T>(CODE_ERROR, message, null);
    }
    public static <T> BaseResponse<T> nullValue(String message) {
        return new BaseResponse<T>(NULL_VALUE, message, null);
    }
    public static <T> BaseResponse<T> errorLogin(String message) {
        return new BaseResponse<T>(ERROR_LOGIN, message, null);
    }
    public static <T> BaseResponse<T> noPower(String message) {
        return new BaseResponse<T>(NO_POWER, message, null);
    }
    public static <T> BaseResponse<T> errWeb(String message) {
        return new BaseResponse<T>(ERROR_WEB, message, null);
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
