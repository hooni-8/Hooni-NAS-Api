package org.hooni.api.common.response;

import lombok.extern.slf4j.Slf4j;
import org.hooni.api.common.code.StatusCode;
import org.hooni.api.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(ApiResponse.error(exception.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation() {
        return ResponseEntity.badRequest().body(ApiResponse.error(StatusCode.INVALID_REQUEST));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException exception) {
        StatusCode statusCode = exception.getStatusCode().value() == HttpStatus.NOT_FOUND.value()
                ? StatusCode.NOT_FOUND
                : StatusCode.ERROR;
        return ResponseEntity.status(exception.getStatusCode())
                .body(ApiResponse.error(statusCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        log.error("Unexpected base-api error", exception);
        return ResponseEntity.internalServerError().body(ApiResponse.error(StatusCode.ERROR));
    }
}
