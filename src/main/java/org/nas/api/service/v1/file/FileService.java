package org.nas.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.file.FileMapper;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.file.FilePreview;
import org.nas.api.model.v1.file.FileView;
import org.nas.api.model.v1.file.PreviewVo;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String BASE_PATH = "/attachfile";

    private static final String BASE_PATH_DEV = "C:/dev/attachfile";

    private static final String TEMP_BASE_PATH = "/data/upload-temp";

    private static final String THUMBNAIL_BASE_PATH = "upload-thumbnail";

    private final FileMapper fileMapper;

    public List<File> getFileList(String userCode, String activeFolderId) {

        FileView fileView = FileView.builder()
                .userCode(userCode)
                .activeFolderId(activeFolderId)
                .build();

        return fileMapper.getFileList(fileView);

    }

    public PreviewVo getThumbnail(String userCode, String fileId) throws IOException {
        FileView fileView = FileView.builder()
                .userCode(userCode)
                .fileId(fileId)
                .build();

        FilePreview preview = fileMapper.getPreview(fileView);
        if (preview == null) {
            return null;
        }

        Path previewPath;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            previewPath = Paths.get(BASE_PATH_DEV, preview.getStoragePath(), preview.getStoredName());
        } else {
            previewPath = Paths.get(BASE_PATH, preview.getStoragePath(), preview.getStoredName());
        }

        if (!Files.exists(previewPath)) {
            return null;
        }

        Resource resource = new UrlResource(previewPath.toUri());
        String contentType = Files.probeContentType(previewPath);
        MediaType mediaType = contentType != null
                ? MediaType.parseMediaType(contentType)
                : MediaType.IMAGE_JPEG;

        return PreviewVo.builder()
                .resource(resource)
                .contentType(contentType)
                .mediaType(mediaType)
                .build();
    }


}
