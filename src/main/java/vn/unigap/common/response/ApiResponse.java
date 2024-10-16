package vn.unigap.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import vn.unigap.common.errorcode.ErrorCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int errorCode;

    private int statusCode;

    private String message;

    private T object;

    public static <T> ApiResponse<T> success(T object) {
        return ApiResponse.<T>builder().errorCode(ErrorCode.SUCCESS).statusCode(HttpStatus.OK.value())
                .message("Successful!").object(object).build();
    }

    public static <T> ApiResponse<T> error(Integer errorCode, HttpStatus httpStatus, String message) {
        return ApiResponse.<T>builder().errorCode(errorCode).statusCode(httpStatus.value()).message(message).build();
    }

}
