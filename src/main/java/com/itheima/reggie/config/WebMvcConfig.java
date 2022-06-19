package com.itheima.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * description 静态配置类
 *
 * @author Administrator
 * @date 2022/6/19-11:23
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /***
     * description: 设置静态资源映射 <p>
     *     配置类
     * </p>
     * @param registry description
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 11:24
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        //将访问URL 映射到相对应的静态资源位置
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
