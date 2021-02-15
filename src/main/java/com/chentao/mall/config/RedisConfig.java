package com.chentao.mall.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Value("${redis.cache.expiration:60}")
    private long expiration;

    // 自定义的CacheManager
    // 当定义了多个cacheManager时，需要加上@Primary指定哪个CacheManager是主要的Manager
//    @Primary
//    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(expiration)) // 设置缓存过期时间
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
                .disableCachingNullValues(); // 禁用缓存空值，不缓存null校验
//                .prefixCacheNameWith("") // 设置key的前缀，生成的缓存键将是：prefix+cache name+“：：”+cache entry key。

        // 使用RedisCacheConfiguration创建RedisCacheManager
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(jackson2JsonRedisSerialize());
        template.setKeySerializer(jackson2JsonRedisSerialize());
        template.setValueSerializer(jackson2JsonRedisSerialize());
        template.afterPropertiesSet();
        return template;
    }
//    @Bean
//    @Primary
//    public RedisCacheManager myCacheManager(RedisConnectionFactory connectionFactory) {
//        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(15))   // 设置缓存过期时间
//                .disableCachingNullValues()     // 禁用缓存空值，不缓存null校验
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerialize()))     // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
//        // 使用RedisCacheConfiguration创建RedisCacheManager
//        RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(cacheConfiguration).build();
//        return cm;
//    }


        // 自定义的RedisTemplate<String, String>方法，在其中定义了新的JSON序列化方法
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//        StringRedisTemplate template = new StringRedisTemplate(factory);
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }
//
    // 使用Jackson2做对象序列化（自定义方式），也可以使用GenericJackson2JsonRedisSerializer()做泛型序列化，第二个比较简单
    private RedisSerializer<?> jackson2JsonRedisSerialize() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

//    /**
//     * SpringBoot 2.X 版本配置方式
//     *
//     * @param redisConnectionFactory
//     * @return
//     */
//    @Bean(name = "cacheManager")
//    @Primary
//    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        //  解决查询缓存转换异常的问题
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        CacheKeyPrefix keyPrefix = new CacheKeyPrefix() {
//            @Override
//            public String compute(String cacheName) {
//                return cacheName + "::";
//            }
//        };
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(3))//失效时间
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
//                .disableCachingNullValues();
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(config)
//                .build();
//
//    }


}


