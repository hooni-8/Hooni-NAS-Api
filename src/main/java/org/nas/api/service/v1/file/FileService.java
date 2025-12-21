package org.nas.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.file.FileMapper;
import org.nas.api.model.v1.file.FileUpload;
import org.nas.api.model.v1.file.TempUploadFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String BASE_PATH = "/attachfile";

    private static final String BASE_PATH_DEV = "C:/dev/attachfile";

    private static final String TEMP_BASE_PATH = "/data/upload-temp";

    private final FileMapper fileMapper;

    // File 생성
    // 비동기 처리를 위해 파일을 임시 저장
    public List<TempUploadFile> createFile(List<MultipartFile> files) throws IOException {
        log.info("========= FILE Create Start =========");
        String os = System.getProperty("os.name").toLowerCase();

        List<TempUploadFile> tempFiles = new ArrayList<>();

        Path tempDir;

        if (os.contains("windows")) {
            tempDir = Paths.get(BASE_PATH_DEV + TEMP_BASE_PATH);
        } else {
            tempDir = Paths.get(BASE_PATH + TEMP_BASE_PATH);
        }

        Files.createDirectories(tempDir);

        for (MultipartFile file : files) {

            Path tempPath = tempDir.resolve(UUID.randomUUID().toString());
            file.transferTo(tempPath.toFile());

            tempFiles.add(
                    new TempUploadFile(
                            file.getOriginalFilename(),
                            tempPath,
                            file.getSize()
                    )
            );

        }

        return tempFiles;
    }

    @Async("uploadExecutor")
    public void uploadAsync(String userCode, String folderId, List<TempUploadFile> files) {

        for (TempUploadFile file : files) {
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

                Path dirPath = Paths.get(BASE_PATH_DEV, relativePath);
                Files.createDirectories(dirPath);

                // 4. 실제 저장
                Path targetPath = dirPath.resolve(storedName);

                // 임시 파일 → 최종 위치 이동
                Files.move(file.getTempPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                FileUpload fileUpload = FileUpload.builder()
                        .folderId(folderId)
                        .userCode(userCode)
                        .originName(originName)
                        .storedName(storedName)
                        .extension(extension)
                        .fileSize(size)
                        .storagePath(relativePath)
                        .build();

                fileMapper.upload(fileUpload);
            } catch (Exception e) {
                log.error("파일 업로드 실패: {} => {}", file.getOriginName(), e.getMessage());
            }
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }


}
