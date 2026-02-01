package org.nas.api.model.v1.file.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nas.api.common.code.StatusCode;
import org.nas.api.model.v1.file.FilePreviewResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviewResponse {
    private String code;

    private String message;

    private FilePreviewResult data;

    public static PreviewResponse getSuccess(FilePreviewResult data) {
        return PreviewResponse.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static PreviewResponse getError() {
        return PreviewResponse.builder()
                .code(StatusCode.ERROR.getCode())
                .message(StatusCode.ERROR.getMessage())
                .build();
    }
}
