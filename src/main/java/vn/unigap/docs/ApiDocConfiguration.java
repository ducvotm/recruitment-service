package vn.unigap.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Vo Tran Minh Duc", email = "vtminhduc140101@gmail.com", url = "https://github.com/ducvotm"), version = "1.0.0", title = "Recruitment Service API Documentation", description = "RESTful API for connecting job seekers with employers", license = @License(name = "Open Source", url = "https://github.com/ducvotm/recruitment-service")), tags = {
        @Tag(name = "Job", description = "Job posting operations"),
        @Tag(name = "Employer", description = "Employer management"),
        @Tag(name = "Seeker", description = "Job seeker profiles"),
        @Tag(name = "Resume", description = "Resume management"),
        @Tag(name = "Metrics", description = "System performance metrics"),
        @Tag(name = "Auth", description = "Authentication operations") }, servers = {
                @Server(url = "http://localhost:8080", description = "Development Server"),
                @Server(url = "https://api.recruitment-service.com", description = "Production Server") })
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", description = "JWT token authentication. Prefix token with 'Bearer '")
@Configuration
public class ApiDocConfiguration {
    // This empty class just serves as a holder for annotations
}