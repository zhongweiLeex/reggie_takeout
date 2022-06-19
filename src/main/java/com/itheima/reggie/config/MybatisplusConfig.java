package com.itheima.reggie.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description 配置Mybatisplus的分页插件<p>
 *     使用下述两个分页接口时，需要配置分页插件
 *     BaseMapper 接口提供了如下几个分页查询接口：
 *    </p>
 *
 *    <p>
 *      1. selectPage：根据 entity 条件，查询全部记录
 *    </p>
 *
 *    <p>
 *      2. selectMapsPage：根据 Wrapper 条件，查询全部记录
 * </p>
 *
 * @author Administrator
 * @date 2022/6/19-21:34
 */
@Configuration
public class MybatisplusConfig {
    /**
     * 分页插件 配置此拦截器， 分页将会不生效
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //配置数据库方言为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
