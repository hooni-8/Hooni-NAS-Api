package org.hooni.api.FilePreviewProvider;

import org.hooni.api.common.code.FileTypeCode;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;

import java.io.IOException;
import java.nio.file.Path;

public interface FilePreviewProvider {

    boolean supports(FileTypeCode fileTypeCode);

    FilePreviewResponse preview(Path path) throws IOException;
}
