package org.hooni.api.service.v1.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.mapper.v1.cleanup.DeletedFileCleanupMapper;
import org.hooni.api.model.v1.file.ExpiredDeletedFile;
import org.hooni.api.properties.DeletedFileCleanupProperties;
import org.hooni.api.properties.FilePathProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletedFileCleanupService {

    private final DeletedFileCleanupMapper cleanupMapper;
    private final FilePathProperties filePathProperties;
    private final DeletedFileCleanupProperties cleanupProperties;

    /**
     * 보관 기간이 지난 논리 삭제 파일을 실제 저장소와 DB에서 영구 삭제한다.
     * 파일 삭제에 실패한 건은 DB 메타데이터를 유지하므로 다음 실행 때 재시도된다.
     */
    @Transactional
    public void purgeExpiredDeletedFiles() {
        int retentionDays = cleanupProperties.getRetentionDays();
        List<ExpiredDeletedFile> expiredFiles = cleanupMapper.selectExpiredDeletedFiles(
                retentionDays,
                cleanupProperties.getBatchSize()
        );

        int purgedCount = 0;
        for (ExpiredDeletedFile file : expiredFiles) {
            try {
                if (!deleteStoredFiles(file)) {
                    continue;
                }

                cleanupMapper.deletePreviewMetadata(file.getFileId());
                int deletedRows = cleanupMapper.deleteExpiredFileMetadata(file.getFileId(), retentionDays);
                if (deletedRows == 1) {
                    purgedCount++;
                } else {
                    log.warn("Deleted file metadata was not removed. fileId={}", file.getFileId());
                }
            } catch (Exception e) {
                log.error("Failed to purge deleted file. fileId={}", file.getFileId(), e);
            }
        }

        // 실제 파일이 없는 폴더 메타데이터도 리프 노드부터 정리한다.
        int purgedFolderCount = cleanupMapper.deleteExpiredEmptyFolders(retentionDays);
        log.info("Deleted-file cleanup completed. files={}, folders={}, retentionDays={}", purgedCount, purgedFolderCount, retentionDays);
    }

    private boolean deleteStoredFiles(ExpiredDeletedFile file) {
        try {
            deleteIfPresent(file.getStoragePath(), file.getStoredName());
            deleteIfPresent(file.getPreviewStoragePath(), file.getPreviewStoredName());
            return true;
        } catch (IOException | IllegalArgumentException e) {
            log.error("Failed to delete stored file. fileId={}", file.getFileId(), e);
            return false;
        }
    }

    private void deleteIfPresent(String storagePath, String storedName) throws IOException {
        if (storagePath == null || storedName == null) {
            return;
        }

        Path basePath = Paths.get(filePathProperties.getBasePath()).toAbsolutePath().normalize();
        Path targetPath = basePath.resolve(storagePath).resolve(storedName).normalize();

        if (!targetPath.startsWith(basePath)) {
            throw new IllegalArgumentException("Refusing to delete a path outside the storage base path");
        }

        Files.deleteIfExists(targetPath);
    }
}
