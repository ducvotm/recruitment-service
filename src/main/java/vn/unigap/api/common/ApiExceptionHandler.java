package vn.unigap.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.ObjectError;
import vn.unigap.api.dto.out.ApiResponse;
import vn.unigap.api.common.ErrorCode;

import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    /*Handles custom ApiException*/
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        return responseEntity(ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
    }

    /*Handles validation errors from method arguments.*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String fieldErrors = ex.getFieldErrors().stream()
                .map(fieldError -> String.format("%s:%s", fieldError.getObjectName(), fieldError.getField()))
                .collect(Collectors.joining(", "));

        String globalErrors = ex.getGlobalErrors().stream()
                .map(ObjectError::getObjectName)
                .collect(Collectors.joining(", "));

        String msg = String.format("Validation errors: fields [%s], global [%s]", fieldErrors, globalErrors);

        return responseEntity(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, msg);
    }

    /*Handles all other generic exceptions*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return responseEntity(ErrorCode.INTERNAL_ERR, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /*The responseEntity method creates an ApiResponse object and wraps it in a ResponseEntity,
    which is then returned to the client.*/
    private ResponseEntity<ApiResponse<Object>> responseEntity(Integer errorCode, HttpStatus status, String msg) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .errorCode(errorCode)
                .statusCode(status.value())
                .message(msg)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }
}
