package org.hooni.api.common.code;

import lombok.Getter;

@Getter
public enum StatusCode {
    SUCCESS("0000", "SUCCESS"),
    INVALID_REQUEST("0001", "INVALID_REQUEST"),
    FORBIDDEN("0002", "FORBIDDEN"),
    UNAUTHORIZED("0003", "UNAUTHORIZED"),
    NOT_FOUND("0004", "NOT_FOUND"),
    ERROR("9999", "ERROR");

    private final String code;
    private final String message;

    StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
