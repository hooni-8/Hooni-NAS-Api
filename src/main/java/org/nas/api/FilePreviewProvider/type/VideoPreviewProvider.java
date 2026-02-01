package org.nas.api.FilePreviewProvider.type;

import org.nas.api.common.code.FileTypeCode;
import org.nas.api.model.v1.file.FilePreviewResult;

import org.nas.api.FilePreviewProvider.FilePreviewProvider;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class VideoPreviewProvider implements FilePreviewProvider {

    @Override
    public boolean supports(FileTypeCode fileTypeCode) {
        return fileTypeCode == FileTypeCode.VIDEO;
    }

    @Override
    public FilePreviewResult preview(Path path) throws IOException {
        String contentType = Files.probeContentType(path);

        return FilePreviewResult.builder()
                .resource(new UrlResource(path.toUri()))
                .contentType(contentType)
                .mediaType(MediaType.parseMediaType(contentType))
                .build();
    }
}
