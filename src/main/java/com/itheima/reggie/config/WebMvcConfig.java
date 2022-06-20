package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

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

    /***
     * description: extendMessageConverters  消息转换器， 自定义个新的 使用Jackson将 java对象转换成JSON
     * @param converters description
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 9:34
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建一个新的消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson 将java对象转换成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将消息转换器对象追加到MVC框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
