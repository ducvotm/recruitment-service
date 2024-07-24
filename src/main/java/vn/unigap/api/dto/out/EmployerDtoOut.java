package vn.unigap.api.dto.out;

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
public class EmployerDtoOut {

    @Email
    @Size(max=255)
    private String email;

    @Size(max=255)
    private String name;

    private String provinceId;

    private String description;

}

