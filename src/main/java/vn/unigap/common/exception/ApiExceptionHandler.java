package vn.unigap.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.ObjectError;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.response.ApiResponse;

import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    // Create logger instance
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /* Handles custom ApiException */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        // Log 4xx errors at DEBUG level
        if (ex.getHttpStatus().is4xxClientError()) {
            logger.debug("Client error occurred (4xx): {}", ex.getMessage(), ex);
        }
        // Log 5xx errors at ERROR level
        else if (ex.getHttpStatus().is5xxServerError()) {
            logger.error("Server error occurred (5xx): {}", ex.getMessage(), ex);
        }
        return responseEntity(ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
    }

    /* Handles validation errors with 4xx status */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String fieldErrors = ex.getFieldErrors().stream()
                .map(fieldError -> String.format("%s:%s", fieldError.getObjectName(), fieldError.getField()))
                .collect(Collectors.joining(", "));

        String globalErrors = ex.getGlobalErrors().stream()
                .map(ObjectError::getObjectName)
                .collect(Collectors.joining(", "));

        String msg = String.format("Validation errors: fields [%s], global [%s]", fieldErrors, globalErrors);

        // Log validation errors (4xx) at DEBUG level
        logger.debug("Validation error occurred: {}", msg, ex);
        return responseEntity(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, msg);
    }

    /* Handles all other generic exceptions with 5xx status */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        // Log generic exceptions (5xx) at ERROR level
        logger.error("Unexpected server error occurred (5xx): {}", ex.getMessage(), ex);
        return responseEntity(ErrorCode.INTERNAL_ERR, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /* Creates a response entity */
    private ResponseEntity<ApiResponse<Object>> responseEntity(Integer errorCode, HttpStatus status, String msg) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .errorCode(errorCode)
                .statusCode(status.value())
                .message(msg)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }
}
