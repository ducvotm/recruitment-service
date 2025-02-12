package vn.unigap.api.dto.in;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthLoginDtoIn {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

}
