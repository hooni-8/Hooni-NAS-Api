package org.nas.api.controller.v1.file;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.file.FilePreviewResult;
import org.nas.api.model.v1.folder.request.ActiveFolderRequest;
import org.nas.api.service.v1.file.FileContentService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "4.FileContent", description = "FileContent")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileContentController extends BaseV1Controller {

    private final FileContentService fileContentService;

    @GetMapping("/thumbnail/{fileId}")
    public ResponseEntity<Resource> getThumbnail(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, ActiveFolderRequest request) throws IOException {
        try {

            FilePreviewResult result = fileContentService.thumbnailFile(userInfo.getUserCode(), fileId, request.getActiveFolderId());

            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .contentType(result.getMediaType())
                    .body(result.getResource());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> previewFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, ActiveFolderRequest request) throws IOException {
        try {

            FilePreviewResult result = fileContentService.previewFile(userInfo.getUserCode(), fileId, request.getActiveFolderId());

            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .contentType(result.getMediaType())
                    .body(result.getResource());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
