package org.nas.api.model.v1.upload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nas.api.common.code.StatusCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {

    private String code;

    private String message;

    public static UploadResponse getSuccess() {
        return UploadResponse.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .build();
    }

    public static UploadResponse getError() {
        return UploadResponse.builder()
                .code(StatusCode.ERROR.getCode())
                .message(StatusCode.ERROR.getMessage())
                .build();
    }
}
