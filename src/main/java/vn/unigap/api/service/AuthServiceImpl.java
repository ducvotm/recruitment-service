package vn.unigap.api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtDecoder jwtDecoder;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserDetailsService userDetailsService, JwtDecoder jwtDecoder, JwtEncoder jwtEncoder,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthLoginDtoOut login(AuthLoginDtoIn loginDtoIn) {
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsService.loadUserByUsername(loginDtoIn.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "invalid credentials");
        }

        if (!passwordEncoder.matches(loginDtoIn.getPassword(), userDetails.getPassword())) {
            throw new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "invalid credentials");
        }

        return AuthLoginDtoOut.builder().accessToken(grantAccessToken(userDetails.getUsername())).build();
    }

/*  * @Override public AuthLoginDtoOut validateAndRefreshAccessToken(String
     * accessToken, String refreshToken) { if (accessToken == null || refreshToken
     * == null) { log.error("Access token or refresh token is null"); throw new
     * ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED,
     * "Access token or refresh token is null"); }
     * 
     * try { // Validate the access token Jwt decodedAccessToken =
     * jwtDecoder.decode(accessToken);
     * 
     * // Check if the access token is expired Instant expiration =
     * decodedAccessToken.getExpiresAt(); if (expiration != null &&
     * Instant.now().isAfter(expiration)) { // Access token is expired, validate the
     * refresh token String username = validateRefreshToken(refreshToken); //
     * Validate refresh token
     * 
     * // Generate a new access token String newAccessToken = grantToken(username,
     * TokenStatus.USED, 15); // 15 minutes expiry
     * 
     * return AuthLoginDtoOut.builder() .accessToken(newAccessToken)
     * .refreshToken(refreshToken) // Return the same refresh token .build(); }
     * 
     * // If access token is still valid, return it without refreshing return
     * AuthLoginDtoOut.builder() .accessToken(accessToken)
     * .refreshToken(refreshToken) .build(); } catch (JwtException e) {
     * log.error("Invalid access token: ", e); throw new
     * ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED,
     * "Invalid access token"); } }*/




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

/*     * private String validateRefreshToken(String refreshToken) { try { Jwt
     * decodedJwt = jwtDecoder.decode(refreshToken); // Decode and validate using
     * public key
     * 
     * // Check if the token has expired Instant expiration =
     * decodedJwt.getExpiresAt(); if (expiration != null &&
     * Instant.now().isAfter(expiration)) { log.error("Refresh token has expired");
     * throw new ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED,
     * "Refresh token has expired"); }
     * 
     * // Extract and return the username from claims return
     * decodedJwt.getSubject(); } catch (JwtException e) {
     * log.error("Invalid refresh token: ", e); throw new
     * ApiException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED,
     * "Invalid refresh token"); } }*/




    private String grantAccessToken(String username) {
        long iat = System.currentTimeMillis() / 1000;
        long exp = iat + Duration.ofHours(8).toSeconds();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(),
                JwtClaimsSet.builder().subject(username).issuedAt(Instant.ofEpochSecond(iat))
                        .expiresAt(Instant.ofEpochSecond(exp)).claim("user_name", username)
                        .claim("scope", List.of("ADMIN")).build());
        try {
            return jwtEncoder.encode(parameters).getTokenValue();
        } catch (JwtEncodingException e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }
}
