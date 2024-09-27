package vn.unigap.api.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisCacheProperties.class)
public class CacheConfig {

    private final RedisCacheProperties redisCacheProperties;

    public CacheConfig(RedisCacheProperties redisCacheProperties) {
        this.redisCacheProperties = redisCacheProperties;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();

        // Register modules for Java 8 date/time (LocalDate, LocalDateTime)
        mapper.registerModule(new JavaTimeModule());

        // Optional: If you need to control date/time formatting, you can add a date/time format here
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(mapper);

    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(redisCacheProperties.getTimeToLiveDefault())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        // Dynamic cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        for (String cacheName : redisCacheProperties.getDynamicNames()) {
            Long ttl = redisCacheProperties.getTtl().get(cacheName);
            Duration cacheTtl = Optional.ofNullable(ttl).map(Duration::ofSeconds).orElse(redisCacheProperties.getTimeToLiveDefault());

            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .disableCachingNullValues()
                    .entryTtl(cacheTtl)
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

            cacheConfigurations.put(cacheName, cacheConfig);
        }

        // Return the RedisCacheManager with the defined configurations
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
