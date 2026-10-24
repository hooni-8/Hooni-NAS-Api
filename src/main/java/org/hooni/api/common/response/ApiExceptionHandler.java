package org.hooni.api.common.response;

import lombok.extern.slf4j.Slf4j;
import org.hooni.api.common.code.StatusCode;
import org.hooni.api.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 영상 탐색·미리보기 닫기 등으로 브라우저가 기존 Range 연결을 취소하면 발생한다.
     * 이미 종료된 응답에는 오류 본문을 다시 쓸 수 없으므로 정상적인 클라이언트 중단으로 처리한다.
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleClientDisconnected(AsyncRequestNotUsableException exception) {
        log.debug("Client closed streaming response: {}", exception.getMessage());
    }

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
