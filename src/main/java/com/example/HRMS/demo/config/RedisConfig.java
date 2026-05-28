package com.example.HRMS.demo.config;

import com.example.HRMS.demo.cache.ActiveWorkerCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ActiveWorkerCache> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        Jackson2JsonRedisSerializer<ActiveWorkerCache> serializer =
                new Jackson2JsonRedisSerializer<>(
                        objectMapper,
                        ActiveWorkerCache.class
                );

        RedisTemplate<String, ActiveWorkerCache> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {

        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(
                    RuntimeException exception,
                    org.springframework.cache.Cache cache,
                    Object key
            ) {

                log.warn(
                        "Redis GET failed for key {}. Falling back to DB.",
                        key
                );
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception,
                    org.springframework.cache.Cache cache,
                    Object key,
                    Object value
            ) {

                log.warn(
                        "Redis PUT failed for key {}.",
                        key
                );
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception,
                    org.springframework.cache.Cache cache,
                    Object key
            ) {

                log.warn(
                        "Redis EVICT failed for key {}.",
                        key
                );
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception,
                    org.springframework.cache.Cache cache
            ) {

                log.warn(
                        "Redis CLEAR failed."
                );
            }
        };
    }
}