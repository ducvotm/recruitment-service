package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.jpa.Employer;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerDtoOut {

    private long id;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String name;

    private Integer province;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    // Covert DtoOut to Employer for saving
    public static EmployerDtoOut from(Employer employer) {
        return EmployerDtoOut.builder()
                .id(employer.getId())
                .email(employer.getEmail())
                .name(employer.getName())
                .province(employer.getProvince())
                .description(employer.getDescription())
                .created_at(employer.getCreatedAt())
                .updated_at(employer.getUpdatedAt()).build();
    }
}