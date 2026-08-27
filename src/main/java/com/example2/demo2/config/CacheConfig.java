package com.example2.demo2.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 夏辰义
 * 2026/8/2414:14
 */
@Configuration
@EnableCaching
//开启 Spring 的缓存注解支持。
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //RedisCacheManager：Spring 提供的 Redis 缓存管理器，是 @Cacheable 和 Redis 之间的"翻译官"。
        //参数 RedisConnectionFactory：Spring Boot 自动注入的 Redis 连接工厂（由 spring.data.redis.* 配置驱动）。
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //创建一个默认的缓存配置模板。
                .entryTtl(Duration.ofMinutes(30))
                //TTL（Time To Live）：缓存条目的默认过期时间，这里设成 30 分钟。
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )//Key 的序列化方式：StringRedisSerializer
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.json())  // ← 用这个替代
                );//Value 的序列化方式：RedisSerializer.json()。

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }//用上面的配置模板，构建一个 RedisCacheManager Bean。
    //Spring 启动后会把它注册到容器中，@Cacheable 注解背后就会用它来读写 Redis。
}
