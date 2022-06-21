package com.itheima.reggie.common;

/**
 * description 自定义业务异常类
 *
 * @author Administrator
 * @date 2022/6/20-20:27
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
