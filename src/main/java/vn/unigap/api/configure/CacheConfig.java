package vn.unigap.api.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Default cache configuration with shared serializer
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                .entryTtl(redisCacheProperties.getTimeToLiveDefault())
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        // Dynamic cache configurations if needed
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        for (String cacheName : redisCacheProperties.getDynamicNames()) {
            Long ttl = redisCacheProperties.getTtl().get(cacheName);
            Duration cacheTtl = Optional.ofNullable(ttl).map(Duration::ofSeconds)
                    .orElse(redisCacheProperties.getTimeToLiveDefault());

            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .disableCachingNullValues().entryTtl(cacheTtl)
                    .serializeKeysWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
            cacheConfigurations.put(cacheName, cacheConfig);
        }

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations).build();
    }
}