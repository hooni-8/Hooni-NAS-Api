package org.hooni.api.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.service.v1.cleanup.TemporaryUploadCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemporaryUploadCleanupScheduler {

    private final TemporaryUploadCleanupService temporaryUploadCleanupService;

    /** 기본값: 매일 오전 4시(Asia/Seoul) */
    @Scheduled(
            cron = "${cleanup.temporary-upload.cron}",
            zone = "${cleanup.temporary-upload.zone}"
    )
    public void cleanupExpiredTemporaryFiles() {
        temporaryUploadCleanupService.purgeExpiredTemporaryFiles();
    }
}
