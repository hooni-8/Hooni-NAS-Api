package org.nas.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.code.FileTypeCode;
import org.nas.api.mapper.v1.file.FileMapper;
import org.nas.api.model.v1.file.FilePreview;
import org.nas.api.model.v1.file.FilePreviewResult;
import org.nas.api.model.v1.file.FileView;
import org.nas.api.FilePreviewProvider.FilePreviewProvider;
import org.nas.api.FilePreviewProvider.FilePreviewProviderResolver;
import org.nas.api.properties.FilePathProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileContentService {

    private final FilePreviewProviderResolver previewHandlerResolver;

    private final FilePathProperties filePathProperties;

    private final FileMapper fileMapper;

    private Path osPath(String path1, String path2) {

        Path path;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            path = Paths.get(filePathProperties.getBasePathDev(), path1, path2);
        } else {
            path = Paths.get(filePathProperties.getBasePath(), path1, path2);
        }

        return path;
    }

    // 썸네일 조회
    public FilePreviewResult thumbnailFile(String userCode, String fileId, String activeFolderId) throws IOException {
        FilePreviewResult result = loadPreview(userCode, fileId, activeFolderId, fileMapper::getThumbnail);

        if (result == null) {
            log.info("!!!!!!!!!!!!!!!!!!!!!");
        }

        return result;
    }

    // 미리보기 조회
    public FilePreviewResult previewFile(String userCode, String fileId, String activeFolderId) throws IOException {
        return loadPreview(userCode, fileId, activeFolderId, fileMapper::getPreview);
    }

    // 파일 조회 공통
    private FilePreviewResult loadPreview(String userCode, String fileId, String activeFolderId, Function<FileView, FilePreview> loader) throws IOException {

        FileView fileView = FileView.builder()
                .userCode(userCode)
                .fileId(fileId)
                .activeFolderId(activeFolderId)
                .build();

        FilePreview filePreview = loader.apply(fileView);
        if (filePreview == null) {
            return null;
        }

        Path path = osPath(filePreview.getStoragePath(), filePreview.getStoredName());
        if (!Files.exists(path)) {
            return null;
        }

        String contentType = Files.probeContentType(path);
        FileTypeCode fileType = FileTypeCode.fromContentType(contentType);

        FilePreviewProvider provider = previewHandlerResolver.resolve(fileType);
        return provider.preview(path);
    }
}
