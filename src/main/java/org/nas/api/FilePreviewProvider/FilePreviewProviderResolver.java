package org.nas.api.FilePreviewProvider;

import org.nas.api.common.code.FileTypeCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilePreviewProviderResolver {

    private final List<FilePreviewProvider> handlers;

    public FilePreviewProviderResolver(List<FilePreviewProvider> handlers) {
        this.handlers = handlers;
    }

    public FilePreviewProvider resolve(FileTypeCode fileTypeCode) {
        return handlers.stream()
                .filter(h -> h.supports(fileTypeCode))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 파일 타입"));
    }
}
