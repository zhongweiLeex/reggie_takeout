package com.itheima.reggie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description TODO
 *
 * @author Administrator
 * @date 2022/6/29-14:55
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {

//    实现看如下的帖子 https://segmentfault.com/a/1190000023040110
    @Autowired
    private RedisTemplate redisTemplate; //自动注入 RedisTemplate

    @Test
    public void testString(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
    }

}
