package org.hooni.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cleanup.temporary-upload")
public class TemporaryUploadCleanupProperties {

    /** 업로드가 중단된 것으로 간주할 임시 파일 보관 시간 */
    private int retentionHours = 24;
}
