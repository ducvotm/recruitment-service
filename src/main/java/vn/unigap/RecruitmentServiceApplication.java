package vn.unigap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
        )
@EnableCaching
public class RecruitmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentServiceApplication.class, args);
    }
}
