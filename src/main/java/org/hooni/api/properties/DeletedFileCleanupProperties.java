package org.hooni.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cleanup.deleted-file")
public class DeletedFileCleanupProperties {

    /** 삭제 후 실제 파일을 보관하는 기간 */
    private int retentionDays = 30;

    /** 한 번의 스케줄 실행에서 처리할 최대 파일 수 */
    private int batchSize = 100;
}
