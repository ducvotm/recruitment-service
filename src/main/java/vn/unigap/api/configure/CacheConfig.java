package vn.unigap.api.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.Duration;
import java.util.Optional;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.redis.time-to-live.default:1m}")
    private Duration defaultTtl;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Enable default typing to include type information in JSON
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // Create the serializer with the custom ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Create default RedisCacheConfiguration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(defaultTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        // Build the RedisCacheManager with default configuration
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }

    @Configuration
    public static class CachingConfiguration implements CacheManagerCustomizer<RedisCacheManager> {

        private final Environment environment;
        private final Duration defaultTtl;

        public CachingConfiguration(Environment environment, @Value("${spring.cache.redis.time-to-live.default:1m}") Duration defaultTtl) {
            this.environment = environment;
            this.defaultTtl = defaultTtl;
        }

        @Override
        public void customize(RedisCacheManager cacheManager) {
            // Dynamically create caches based on cache names used in @Cacheable annotations
            cacheManager.getCacheNames().forEach(cacheName -> {
                // Get the TTL for the cache name from application properties (if available)
                String ttlProperty = "caching.configurations." + cacheName + ".ttl";
                Duration ttl = Optional.ofNullable(environment.getProperty(ttlProperty, Long.class))
                        .map(Duration::ofSeconds)
                        .orElse(defaultTtl);

                // Create a new cache configuration with the specific TTL
                RedisCacheConfiguration newConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl);
                cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).putIfAbsent(name, newConfiguration));
            });
        }
    }
}
