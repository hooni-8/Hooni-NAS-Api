package org.nas.api.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.service.v1.cleanup.DeletedFileCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedFileCleanupScheduler {

    private final DeletedFileCleanupService deletedFileCleanupService;

    /** 기본값: 매일 오전 3시 30분(Asia/Seoul) */
    @Scheduled(
            cron = "${cleanup.deleted-file.cron}",
            zone = "${cleanup.deleted-file.zone}"
    )
    public void cleanupExpiredDeletedFiles() {
        log.info("Starting deleted-file cleanup");
        deletedFileCleanupService.purgeExpiredDeletedFiles();
    }
}
