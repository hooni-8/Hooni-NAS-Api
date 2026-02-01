package org.nas.api.FilePreviewProvider;

import org.nas.api.common.code.FileTypeCode;
import org.nas.api.model.v1.file.FilePreviewResult;

import java.io.IOException;
import java.nio.file.Path;

public interface FilePreviewProvider {

    boolean supports(FileTypeCode fileTypeCode);

    FilePreviewResult preview(Path path) throws IOException;
}
