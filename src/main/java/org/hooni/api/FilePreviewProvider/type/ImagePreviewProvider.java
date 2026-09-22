package org.hooni.api.FilePreviewProvider.type;

import org.hooni.api.FilePreviewProvider.FilePreviewProvider;
import org.hooni.api.common.code.FileTypeCode;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ImagePreviewProvider implements FilePreviewProvider {

    @Override
    public boolean supports(FileTypeCode fileTypeCode) {
        return fileTypeCode == FileTypeCode.IMAGE;
    }

    @Override
    public FilePreviewResponse preview(Path path) throws IOException {
        String contentType = Files.probeContentType(path);

        return FilePreviewResponse.builder()
                .resource(new UrlResource(path.toUri()))
                .contentType(contentType)
                .mediaType(MediaType.parseMediaType(contentType))
                .build();
    }
}
