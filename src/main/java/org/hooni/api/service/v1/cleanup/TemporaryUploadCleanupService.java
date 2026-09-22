package org.hooni.api.service.v1.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.properties.FilePathProperties;
import org.hooni.api.properties.TemporaryUploadCleanupProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemporaryUploadCleanupService {

    private final FilePathProperties filePathProperties;
    private final TemporaryUploadCleanupProperties cleanupProperties;

    /**
     * 전송 중 브라우저 종료·서버 재시작 등으로 남은 임시 파일을 삭제한다.
     * 현재 업로드 중인 파일을 지우지 않도록 마지막 수정 시각이 보관 시간을 넘은 파일만 처리한다.
     */
    public void purgeExpiredTemporaryFiles() {
        Path tempDirectory = getSafeTempDirectory();
        if (!Files.isDirectory(tempDirectory)) {
            return;
        }

        Instant cutoff = Instant.now().minus(cleanupProperties.getRetentionHours(), ChronoUnit.HOURS);
        int deletedCount = 0;

        try (Stream<Path> paths = Files.list(tempDirectory)) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                try {
                    if (!Files.isRegularFile(path)) {
                        continue;
                    }

                    FileTime lastModifiedTime = Files.getLastModifiedTime(path);
                    if (!lastModifiedTime.toInstant().isBefore(cutoff)) {
                        continue;
                    }

                    if (Files.deleteIfExists(path)) {
                        deletedCount++;
                    }
                } catch (IOException e) {
                    log.warn("Failed to delete temporary upload file. path={}", path, e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to clean temporary upload files. directory={}", tempDirectory, e);
            return;
        }

        log.info("Temporary upload cleanup completed. files={}, retentionHours={}", deletedCount, cleanupProperties.getRetentionHours());
    }

    private Path getSafeTempDirectory() {
        Path basePath = Paths.get(filePathProperties.getBasePath()).toAbsolutePath().normalize();
        Path tempDirectory = Paths.get(
                filePathProperties.getBasePath(),
                filePathProperties.getTempBasePath()
        ).toAbsolutePath().normalize();

        if (!tempDirectory.startsWith(basePath)) {
            throw new IllegalArgumentException("Refusing to clean a temporary directory outside the storage base path");
        }

        return tempDirectory;
    }
}
