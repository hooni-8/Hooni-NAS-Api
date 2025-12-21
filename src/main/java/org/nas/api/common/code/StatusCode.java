package org.nas.api.common.code;

import lombok.Getter;

@Getter
public enum StatusCode {
    SUCCESS("0000", "SUCCESS")
    ,ERROR("9999", "ERROR")
    ,ERROR_VALID("0001", "ERROR_VALID")
    ,FORBIDDEN("0002", "FORBIDDEN")
    ,FAIL_STATUS_N("0003", "FAIL_STATUS_N")
    ,FAIL_SELF_DELETE("0004", "FAIL_SELF_DELETE")
    ,FAIL_EMPTY("0005","FAIL_EMPTY")
    ,MAX_FILE_SIZE("0006", "MAX_FILE_SIZE")
    ,FAIL_LIST_EMPTY("0007","FAIL_LIST_EMPTY");

    private final String code;

    private final String message;

    StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
