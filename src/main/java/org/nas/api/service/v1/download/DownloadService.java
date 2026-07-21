package org.nas.api.service.v1.download;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.download.DownloadMapper;
import org.nas.api.model.v1.download.FileDownload;
import org.nas.api.properties.FilePathProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {

    private final FilePathProperties filePathProperties;

    private final DownloadMapper downloadMapper;

    public ResponseEntity<Resource> downloadSingle(String userCode, String fileId, String folderId) throws IOException {

        FileDownload fileInfo = downloadMapper.selectDownloadFileInfo(userCode, fileId, folderId);

        Path dirPath = Paths.get(filePathProperties.getBasePath(), fileInfo.getStoragePath(), fileInfo.getStoredName());

        String fileName = fileInfo.getOriginName() + fileInfo.getExtension();

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        Resource resource = new FileSystemResource(dirPath);

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName
                )
                .body(resource);
    }

}
