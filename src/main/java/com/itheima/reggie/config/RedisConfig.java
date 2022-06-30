package com.itheima.reggie.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * description:  Redis配置类
 *
 * @author Administrator
 * @date 2022/6/29-14:57
 */
@Configuration
public class RedisConfig {

    @Bean
    /**
     * description: redisTemplate 自定义 redisTemplate 但是如果自己不配置 ， springboot 框架也会自动创建Bean对象
     * @param redisConnectionFactory 连接工厂
     * @return org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.Object>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/29 - 20:16
     */
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisSerializer<Object> serializer = redisSerializer(); //使用自定义的 JSON序列化器 ， 默认使用 JDKSerializationRedisSerializer 序列化器
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * description: redisSerializer 设置value 的 JSON序列化器
     * @return org.springframework.data.redis.serializer.RedisSerializer<java.lang.Object>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/29 - 16:18
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        //创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //解决jackson2 无法反序列化 LocalDataTime问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); 有漏洞 deprecated
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    /**
     * description: <br> redisCacheManager 自定义缓存读写机制 <br> https://blog.csdn.net/f641385712/article/details/95047822 <br> redis 缓存管理器 <br> 设置ttl生存时间 ，写入缓存方式（nonLock / Lock ）
     * @param redisConnectionFactory description
     * @return org.springframework.data.redis.cache.RedisCacheManager
     * @throws
     * @author zhongweileex
     * @date: 2022/6/29 - 16:40
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //无锁写入缓存
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置Redis缓存有效期为1天
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()))
                .entryTtl(Duration.ofDays(1)) //Duration.ZERO表示永不过期（此值一般建议必须设置）
                //.disableKeyPrefix() // 禁用key的前缀
                //.disableCachingNullValues() //禁止缓存null值
                //=== 前缀我个人觉得是非常重要的，建议约定：注解缓存一个统一前缀、RedisTemplate直接操作的缓存一个统一前缀===
                //.prefixKeysWith("baidu:") // 底层其实调用的还是computePrefixWith() 方法，只是它的前缀是固定的（默认前缀是cacheName，此方法是把它固定住，一般不建议使用固定的）
                //.computePrefixWith(CacheKeyPrefix.simple()); // 使用内置的实现
//                .computePrefixWith(cacheName -> "caching:" + cacheName) // 自己实现，建议这么使用(cacheName也保留下来了)
                ;
        //设置默认的cache组件
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                // .disableCreateOnMissingCache() // 关闭动态创建Cache
                //.initialCacheNames() // 初始化时候就放进去的cacheNames（若关闭了动态创建，这个就是必须的）
                .cacheDefaults(redisCacheConfiguration) // 默认配置（强烈建议配置上）。  比如动态创建出来的都会走此默认配置
                //.withInitialCacheConfigurations() // 个性化配置  可以提供一个Map，针对每个Cache都进行个性化的配置（否则是默认配置）
                //.transactionAware() // 支持事务
                .build();
        return redisCacheManager;
    }

}
