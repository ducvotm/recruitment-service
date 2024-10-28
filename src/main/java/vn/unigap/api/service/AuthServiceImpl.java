package vn.unigap.api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.unigap.api.dto.in.AuthLoginDtoIn;
import vn.unigap.api.dto.out.AuthLoginDtoOut;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;
import vn.unigap.common.enums.TokenStatus;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserDetailsService userDetailsService, JwtEncoder jwtEncoder,
                           PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthLoginDtoOut login(AuthLoginDtoIn loginDtoIn) {
        UserDetails userDetails = getUserDetails(loginDtoIn.getUsername());
        validatePassword(loginDtoIn.getPassword(), userDetails.getPassword());

        // Set token status to INIT when generating tokens
        return AuthLoginDtoOut.builder()
                .accessToken(grantToken(userDetails.getUsername(), TokenStatus.INIT, 15))
                .refreshToken(grantToken(userDetails.getUsername(), TokenStatus.INIT, 30 * 24 * 60)) // 30 days
                .build();
    }

    @Override
    public AuthLoginDtoOut refreshAccessToken(String refreshToken) {
        String username = validateRefreshToken(refreshToken);
        // Generate a new access token
        String newAccessToken = grantToken(username, TokenStatus.USED, 15);
        return AuthLoginDtoOut.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Optionally return the same refresh token
                .build();
    }

    private UserDetails getUserDetails(String username) {
        try {
            return userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("User not found: {}", username);
            throw new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Invalid credentials");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("Invalid password attempt");
            throw new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Invalid credentials");
        }
    }

    private String validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(jwtSecretKey) // Ensure you use the same signing key
                    .parseClaimsJws(refreshToken);
            return claims.getBody().getSubject(); // Extract username from claims
        } catch (ExpiredJwtException e) {
            log.warn("Expired refresh token");
            throw new ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, "Refresh token expired");
        } catch (JwtException e) {
            log.error("Invalid refresh token: ", e);
            throw new ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    private String grantToken(String username, TokenStatus status, long expiryMinutes) {
        long iat = System.currentTimeMillis() / 1000;
        long exp = iat + Duration.ofMinutes(expiryMinutes).toSeconds();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).build(),
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(Instant.ofEpochSecond(iat))
                        .expiresAt(Instant.ofEpochSecond(exp))
                        .claim("user_name", username)
                        .claim("roles", List.of("ADMIN", "USER")) // Adding roles as claims
                        .claim("token_status", status.getStatus()) // Attach token status as a claim
                        .build()
        );

        try {
            return jwtEncoder.encode(parameters).getTokenValue();
        } catch (JwtEncodingException e) {
            log.error("Error generating token for user {}: {}", username, e.getMessage());
            throw new RuntimeException("Error generating token", e);
        }
    }
}
