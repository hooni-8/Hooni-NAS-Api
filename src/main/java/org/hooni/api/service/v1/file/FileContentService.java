package org.hooni.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.FilePreviewProvider.FilePreviewProviderResolver;
import org.hooni.api.common.code.FileTypeCode;
import org.hooni.api.mapper.v1.file.FileMapper;
import org.hooni.api.model.v1.file.FilePreview;
import org.hooni.api.model.v1.file.FileView;
import org.hooni.api.FilePreviewProvider.FilePreviewProvider;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.hooni.api.properties.FilePathProperties;
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

    // 썸네일 조회
    public FilePreviewResponse thumbnailFile(String userCode, String fileId, String folderId) throws IOException {
        return loadPreview(userCode, fileId, folderId, fileMapper::getThumbnail);
    }

    // 미리보기 조회
    public FilePreviewResponse previewFile(String userCode, String fileId, String folderId) throws IOException {
        return loadPreview(userCode, fileId, folderId, fileMapper::getPreview);
    }

    // 파일 조회 공통
    private FilePreviewResponse loadPreview(String userCode, String fileId, String folderId, Function<FileView, FilePreview> loader) throws IOException {

        FileView fileView = FileView.builder()
                .userCode(userCode)
                .fileId(fileId)
                .folderId(folderId)
                .build();

        FilePreview filePreview = loader.apply(fileView);
        if (filePreview == null) {
            return null;
        }

        Path path = Paths.get(filePathProperties.getBasePath(), filePreview.getStoragePath(), filePreview.getStoredName());
        if (!Files.exists(path)) {
            return null;
        }

        String contentType = Files.probeContentType(path);
        FileTypeCode fileType = FileTypeCode.fromContentType(contentType);

        FilePreviewProvider provider = previewHandlerResolver.resolve(fileType);
        return provider.preview(path);
    }
}
