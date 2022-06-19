package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * description 启动类
 *
 * @author lizhongwei
 * @date 2022/6/19-11:15
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan//扫描 webFilter之类的注解
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");
    }
}
