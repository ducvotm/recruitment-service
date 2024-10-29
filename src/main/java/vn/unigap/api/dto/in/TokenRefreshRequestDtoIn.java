package vn.unigap.api.dto.in;

import lombok.Data;

@Data
public class TokenRefreshRequestDtoIn {
    String accessToken;
    String refreshToken;
}
