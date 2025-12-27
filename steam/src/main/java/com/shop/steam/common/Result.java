package com.shop.steam.common;

import lombok.Data;

/**
 * 统一结果返回类
 * 让后端返回给前端的数据格式保持一致：{code: 200, msg: "成功", data: {...}}
 */
@Data
public class Result<T> {
    private Integer code; // 状态码：200-成功，500-失败
    private String msg;   // 提示信息
    private T data;       // 返回的具体数据

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "操作成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = 500;
        r.msg = msg;
        return r;
    }
}