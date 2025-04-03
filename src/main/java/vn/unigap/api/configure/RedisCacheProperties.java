package vn.unigap.api.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.cache.redis")
public class RedisCacheProperties {

    private Duration timeToLiveDefault = Duration.ofMinutes(1);
    private String[] dynamicNames = new String[] {};
    private Map<String, Long> ttl = Map.of(); // Map để lưu TTL cho từng cache

    // Getters và Setters
    public Duration getTimeToLiveDefault() {
        return timeToLiveDefault;
    }

    public void setTimeToLiveDefault(Duration timeToLiveDefault) {
        this.timeToLiveDefault = timeToLiveDefault;
    }

    public String[] getDynamicNames() {
        return dynamicNames;
    }

    public void setDynamicNames(String[] dynamicNames) {
        this.dynamicNames = dynamicNames;
    }

    public Map<String, Long> getTtl() {
        return ttl;
    }

    public void setTtl(Map<String, Long> ttl) {
        this.ttl = ttl;
    }
}