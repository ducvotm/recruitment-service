package vn.unigap.api.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;
}