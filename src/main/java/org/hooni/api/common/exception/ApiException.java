package org.hooni.api.common.exception;

import lombok.Getter;
import org.hooni.api.common.code.StatusCode;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final StatusCode statusCode;

    public ApiException(HttpStatus httpStatus, StatusCode statusCode) {
        super(statusCode.getMessage());
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
    }
}
