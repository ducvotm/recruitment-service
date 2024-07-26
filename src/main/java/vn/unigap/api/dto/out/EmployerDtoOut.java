package vn.unigap.api.dto.out;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.Employer;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerDtoOut {

    private long id;

    @Email
    @Size(max=255)
    private String email;

    @Size(max=255)
    private String name;

    private String provinceId;
    private String description;
    private Date created_at = new Date();
    private Date updated_at = new Date();

    //Covert DtoOut to Employer for saving
    public static EmployerDtoOut from(Employer employer) {
        return EmployerDtoOut.builder()
                .id(employer.getId())
                .email(employer.getEmail())
                .name(employer.getName())
                .provinceId(employer.getProvinceId())
                .description(employer.getDescription())
                .created_at(employer.getCreated_at())
                .updated_at(employer.getUpdated_at())
                .build();
    }
}

