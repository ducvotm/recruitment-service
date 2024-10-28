package vn.unigap.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.unigap.api.dto.in.AuthLoginDtoIn;
import vn.unigap.api.dto.in.RefreshTokenDtoIn;
import vn.unigap.api.dto.out.AuthLoginDtoOut;
import vn.unigap.api.service.AuthService;
import vn.unigap.common.controller.AbstractResponseController;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;

@RestController
@RequestMapping(value = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth", description = "Auth")
public class AuthController extends AbstractResponseController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", responses = {@ApiResponse(responseCode = "200", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseLogin.class))})})
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginDtoIn loginDtoIn) {
        return responseEntity(() -> {
            return this.authService.login(loginDtoIn);
        });
    }

    @Operation(summary = "Refresh Access Token", responses = {@ApiResponse(responseCode = "200", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseRefresh.class))})})
    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenDtoIn refreshTokenDtoIn) {
        return responseEntity(() -> {
            return this.authService.refreshAccessToken(refreshTokenDto.getRefreshToken());
        });
    }



    private static class ResponseLogin extends vn.unigap.common.response.ApiResponse<AuthLoginDtoOut> {
    }

    private static class ResponseRefresh extends vn.unigap.common.response.ApiResponse<AuthLoginDtoOut> {
    }
}
