package org.nas.api.model.v1.volume.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nas.api.common.code.StatusCode;
import org.nas.api.model.v1.volume.DiskVolume;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiskVolumeResponse {

    private String code;
    private String message;

    private DiskVolume data;

    public static DiskVolumeResponse getSuccess(DiskVolume diskVolume) {
        return DiskVolumeResponse.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(diskVolume)
                .build();
    }

    public static DiskVolumeResponse getError() {
        return DiskVolumeResponse.builder()
                .code(StatusCode.ERROR.getCode())
                .message(StatusCode.ERROR.getMessage())
                .build();
    }
}
