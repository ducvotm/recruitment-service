package vn.unigap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@SpringBootApplication
@EnableCaching
public class RecruitmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecruitmentServiceApplication.class, args);
	}
}


