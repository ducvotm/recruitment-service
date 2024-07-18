package vn.unigap.api.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    @Email
    @Size(max=255)
    private String email;

    @NotEmpty
    @Size(max=255)
    private String name;

    @NotEmpty
    private Integer provinceId;

    private String description;

}
