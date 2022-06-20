package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * description 自定义元数据对象处理器， 实现自动填充 Employee 类的字段
 * <p>
 *     必须添加 @Component注解 <br>
 * </p>
 *
 * @author Administrator
 * @date 2022/6/20-15:09
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /***
     * description: 插入操作自动填充
     * @param metaObject 传入的元数据
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 15:19
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("公共字段自动填充 insert");
        log.info(metaObject.toString());
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"createUser", Long.class,BaseContext.getCurrentId());
        this.strictInsertFill(metaObject,"updateUser", Long.class,BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充 update ");
        log.info(metaObject.toString());
        this.strictUpdateFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        this.strictUpdateFill(metaObject,"updateUser", Long.class,BaseContext.getCurrentId());

    }
}
