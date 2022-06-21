package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * description 全局异常处理器 <p>
 *     就是通过通知实现
 * </p>
 *
 * @author Administrator
 * @date 2022/6/19-20:39
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//定义对哪些Controller进行异常捕获
@ResponseBody //将结果封装成JSON数据返回
@Slf4j
public class GlobalExceptionHandler {
    /***
     * description: exceptionHandler 异常处理方法
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 20:43
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //表示此方法为异常处理类
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        /*
            判断是否是 重复实体类异常
         */
        if(ex.getMessage().contains("Duplicate entry")){

            String[] s = ex.getMessage().split(" ");
            String msg = s[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /***
     * description: 自定义异常处理器
     * @param ex description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 20:32
     */
    @ExceptionHandler(CustomException.class) //表示此方法为异常处理类
    public R<String> customExceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

}
