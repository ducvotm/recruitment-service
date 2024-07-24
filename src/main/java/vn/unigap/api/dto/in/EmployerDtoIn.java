package vn.unigap.api.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerDtoIn {

    @Email
    @Size(max=255)
    private String email;

    @Size(max=255)
    private String name;

    private String provinceId;

    private String description;

}
