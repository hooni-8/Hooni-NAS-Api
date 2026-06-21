package org.nas.api.FilePreviewProvider;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class VideoTokenPayload {

    private String userCode;
    private String fileId;
}