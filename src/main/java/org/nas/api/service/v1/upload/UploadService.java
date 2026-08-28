package org.nas.api.service.v1.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.nas.api.common.utils.FileUtils;
import org.nas.api.mapper.v1.upload.UploadMapper;
import org.nas.api.model.v1.upload.FileUpload;
import org.nas.api.model.v1.upload.PreviewUpload;
import org.nas.api.model.v1.upload.TempUploadFile;
import org.nas.api.properties.FilePathProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.io.FilenameUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadMapper uploadMapper;

    private final FilePathProperties filePathProperties;

    // File 생성
    // 비동기 처리를 위해 파일을 임시 저장
    public TempUploadFile createFile(MultipartFile file) throws IOException {
        log.info("========= FILE Create Start =========");

        Path tempDir = Paths.get(filePathProperties.getBasePath(), filePathProperties.getTempBasePath());

        Files.createDirectories(tempDir);

        Path tempPath = tempDir.resolve(UUID.randomUUID().toString());
        try {
            file.transferTo(tempPath.toFile());
        } catch (IOException e) {
            Files.deleteIfExists(tempPath);
            throw e;
        }

        return TempUploadFile.builder()
                .originName(file.getOriginalFilename())
                .tempPath(tempPath)
                .size(file.getSize())
                .build();

    }

    @Transactional(rollbackFor = Exception.class)
    public void upload(String userCode, String folderId, long lastModifiedAt, TempUploadFile file) throws IOException {
        log.info("========= FILE Upload Start =========");

        Path targetPath = null;
        try {
            // 1. 원본 정보
            String originName = FilenameUtils.getBaseName(file.getOriginName());
            String extension = FileUtils.extractExtension(file.getOriginName());
            long size = file.getSize();

            // 2. UUID 파일명
            String storedName = UUID.randomUUID().toString() + extension;

            // 3. 날짜 디렉터리
            LocalDate today = LocalDate.now();
            String relativePath = String.format(
                    "%d/%02d/%02d",
                    today.getYear(),
                    today.getMonthValue(),
                    today.getDayOfMonth()
            );

            Path dirPath = Paths.get(filePathProperties.getBasePath(), relativePath);

            Files.createDirectories(dirPath);

            // 4. 실제 저장
            targetPath = dirPath.resolve(storedName);

            // 임시 파일 → 최종 위치 이동
            Files.move(file.getTempPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Timestamp lastModifiedDate = new Timestamp(lastModifiedAt);

            String fileId = UUID.randomUUID().toString();

            FileUpload fileUpload = FileUpload.builder()
                    .fileId(fileId)
                    .folderId(folderId)
                    .userCode(userCode)
                    .originName(originName)
                    .storedName(storedName)
                    .extension(extension)
                    .fileSize(size)
                    .storagePath(relativePath)
                    .lastModifiedAt(lastModifiedDate)
                    .build();

            uploadMapper.upload(fileUpload);

            if (FileUtils.isImage(extension) || FileUtils.isVideo(extension)) {

                createThumbnail(dirPath, storedName, extension, fileId, relativePath, true);

            }

        } catch (Exception e) {
            deleteFileQuietly(targetPath);
            log.error("파일 업로드 실패: {}", file.getOriginName(), e);

            if (e instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("파일 저장 또는 메타데이터 등록에 실패했습니다.", e);
        } finally {
            // 이동 전 실패하거나 서버 처리 중 오류가 나도 임시 파일을 남기지 않는다.
            deleteFileQuietly(file.getTempPath());
        }
    }

    public void createThumbnail(Path dirPath, String storedName, String extension, String fileId, String relativePath, boolean isSave) throws IOException {

        boolean isImage = FileUtils.isImage(extension);
        boolean isVideo = FileUtils.isVideo(extension);

        if (!isImage && !isVideo) {
            return;
        }

        Path originalPath = dirPath.resolve(storedName);

        Path thumbnailDir = dirPath.resolve(filePathProperties.getThumbnailBasePath());
        Files.createDirectories(thumbnailDir);

        boolean isPng = extension.equalsIgnoreCase(".png");

        String thumbExt = isPng ? ".png" : ".jpg";
        String baseName = storedName.substring(0, storedName.lastIndexOf("."));
        String thumbnailName = "thumbnail_" + baseName + thumbExt;
        Path thumbnailPath = thumbnailDir.resolve(thumbnailName);

        boolean thumbnailCreated;
        if (isVideo) {
            thumbnailCreated = createVideoThumbnail(originalPath, thumbnailPath);
        } else {
            thumbnailCreated = createImageThumbnail(originalPath, thumbnailPath, isPng);
        }

        if (!thumbnailCreated) {
            log.warn("Thumbnail metadata is not saved because thumbnail creation failed: {}", originalPath);
            return;
        }

        if (isSave) {
            PreviewUpload filePreview = PreviewUpload.builder()
                    .fileId(fileId)
                    .storedName(thumbnailName)
                    .storagePath(Paths.get(relativePath, filePathProperties.getThumbnailBasePath()).toString())
                    .build();

            uploadMapper.createPreview(filePreview);
        }
    }

    // 이미지 썸네일 생성
    private boolean createImageThumbnail(Path original, Path thumbnail, boolean isPng) {
        try {
            Thumbnails.of(original.toFile())
                    .size(300, 300)
                    .outputFormat(isPng ? "png" : "jpg")
                    .outputQuality(isPng ? 1.0f : 0.9f)
                    .keepAspectRatio(true)
                    .toFile(thumbnail.toFile());
            return Files.isRegularFile(thumbnail);
        } catch (Exception e) {
            log.warn("Thumbnail Create failed: {}", original, e);
            return false;
        }
    }

    // 비디오 썸네일 생성
    private boolean createVideoThumbnail(Path original, Path thumbnailPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    filePathProperties.getFfmpegPath(),
                    "-y",
                    "-ss", "00:00:01",
                    "-i", original.toAbsolutePath().toString(),
                    "-vframes", "1",
                    "-q:v", "2",
                    thumbnailPath.toAbsolutePath().toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[FFmpeg] {}", line);
                }
            }

            int exitCode = process.waitFor();
            log.info("FFmpeg finished. exitCode={}", exitCode);
            return exitCode == 0 && Files.isRegularFile(thumbnailPath);

        } catch (Exception e) {
            log.warn("Video thumbnail create failed: {}", original, e);
            return false;
        }
    }

    private void deleteFileQuietly(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("파일 정리에 실패했습니다. path={}", path, e);
        }
    }

}
