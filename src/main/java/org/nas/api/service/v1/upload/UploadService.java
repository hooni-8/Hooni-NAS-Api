package org.nas.api.service.v1.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.nas.api.mapper.v1.upload.UploadMapper;
import org.nas.api.model.v1.upload.FileUpload;
import org.nas.api.model.v1.upload.PreviewUpload;
import org.nas.api.model.v1.upload.TempUploadFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private static final String BASE_PATH = "/attachfile";

    private static final String BASE_PATH_DEV = "C:/dev/attachfile";

    private static final String TEMP_BASE_PATH = "/data/upload-temp";

    private static final String THUMBNAIL_BASE_PATH = "upload-thumbnail";

    private final UploadMapper uploadMapper;

    // File 생성
    // 비동기 처리를 위해 파일을 임시 저장
    public TempUploadFile createFile(MultipartFile file) throws IOException {
        log.info("========= FILE Create Start =========");
        String os = System.getProperty("os.name").toLowerCase();

        Path tempDir;

        if (os.contains("windows")) {
            tempDir = Paths.get(BASE_PATH_DEV + TEMP_BASE_PATH);
        } else {
            tempDir = Paths.get(BASE_PATH + TEMP_BASE_PATH);
        }

        Files.createDirectories(tempDir);

        Path tempPath = tempDir.resolve(UUID.randomUUID().toString());
        file.transferTo(tempPath.toFile());

        return TempUploadFile.builder()
                .originName(file.getOriginalFilename())
                .tempPath(tempPath)
                .size(file.getSize())
                .build();

    }

    @Async("uploadExecutor")
    public void uploadAsync(String userCode, String folderId, long lastModifiedAt, TempUploadFile file) {
        log.info("========= FILE Upload Start =========");
        String os = System.getProperty("os.name").toLowerCase();

        try {
            // 1. 원본 정보
            String originName = file.getOriginName();
            String extension = extractExtension(originName);
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

            Path dirPath;
            if (os.contains("windows")) {
                dirPath = Paths.get(BASE_PATH_DEV, relativePath);
            } else {
                dirPath = Paths.get(BASE_PATH, relativePath);
            }

            Files.createDirectories(dirPath);

            // 4. 실제 저장
            Path targetPath = dirPath.resolve(storedName);

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

            if (isImage(extension)) {

                Path originalPath = dirPath.resolve(storedName);

                Path thumbnailDir = dirPath.resolve(THUMBNAIL_BASE_PATH);
                Files.createDirectories(thumbnailDir);

                boolean isPng = extension.equalsIgnoreCase(".png");

                String thumbExt = isPng ? ".png" : ".jpg";
                String baseName = storedName.substring(0, storedName.lastIndexOf("."));
                String thumbnailName = "thumbnail_" + baseName + thumbExt;
                Path thumbnailPath = thumbnailDir.resolve(thumbnailName);

                createImageThumbnail(originalPath, thumbnailPath, isPng);

                PreviewUpload filePreview = PreviewUpload.builder()
                        .fileId(fileId)
                        .storedName(thumbnailName)
                        .storagePath(Paths.get(relativePath, THUMBNAIL_BASE_PATH).toString())
                        .build();

                uploadMapper.createPreview(filePreview);
            }

        } catch (Exception e) {
            log.error("파일 업로드 실패: {} => {}", file.getOriginName(), e.getMessage());
        }
    }

    // 썸네일 생성
    private void createImageThumbnail(Path original, Path thumbnail, boolean isPng) throws IOException {
        try {
            if (isPng) {
                Thumbnails.of(original.toFile())
                        .size(300, 300)
                        .outputFormat("png")
                        .keepAspectRatio(true)
                        .toFile(thumbnail.toFile());
            } else {
                Thumbnails.of(original.toFile())
                        .size(300, 300)
                        .outputFormat("jpg")
                        .outputQuality(0.9f)
                        .keepAspectRatio(true)
                        .toFile(thumbnail.toFile());
            }
        } catch (Exception e) {
            log.warn("Thumbnail Create failed: {}", original);
        }
    }

    // 파일 확장자 추출
    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf(".");

        return (idx == -1) ? "" : filename.substring(idx).toLowerCase();
    }

    // 이미지 여부
    private boolean isImage(String extension) {
        return List.of(".jpg", ".jpeg", ".png", ".gif", ".webp")
                .contains(extension.toLowerCase());
    }
}
