package vn.unigap.api.dto.out;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.Employer;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployerDtoOut {

    private long id;

    @Email
    @Size(max=255)
    private String email;

    @Size(max=255)
    private String name;


    private Integer province;
    private String description;
    private LocalDateTime updated_at;

    // Create a new UpdateEmployerDtoOut from an Employer entity
    public static UpdateEmployerDtoOut from(Employer employer) {
        return UpdateEmployerDtoOut.builder()
                .id(employer.getId())
                .email(employer.getEmail())
                .name(employer.getName())
                .province(employer.getProvince())
                .description(employer.getDescription())
                .updated_at(employer.getUpdated_at())
                .build();
    }
}