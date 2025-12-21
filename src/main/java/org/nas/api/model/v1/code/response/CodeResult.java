package org.nas.api.model.v1.code.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nas.api.common.code.StatusCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeResult {

    private String code;
    private String message;

    public static CodeResult getSuccess() {
        return CodeResult.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .build();
    }

    public static CodeResult getError() {
        return CodeResult.builder()
                .code(StatusCode.ERROR.getCode())
                .message(StatusCode.ERROR.getMessage())
                .build();
    }

    public static CodeResult getErrorCode(String code, String message) {
        return CodeResult.builder()
                .code(code)
                .message(message)
                .build();
    }
}