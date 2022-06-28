package com.itheima.reggie.common;

/**
 * description 工具类<br> 封装ThreadLocal get() set() 方法
 *
 * @author Administrator
 * @date 2022/6/20-15:43
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * description: setCurrentId 设置 threadLocal 存储用户id, 用户id 从session中获得
     * @param id 用户id
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 15:59
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    /***
     * description: getCurrentId 获取threadLocal 中存储的 用户id
     * @return java.lang.Long
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 15:46
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
