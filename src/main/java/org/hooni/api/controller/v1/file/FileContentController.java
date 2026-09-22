package org.hooni.api.controller.v1.file;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.hooni.api.model.v1.folder.request.FolderRequest;
import org.hooni.api.service.v1.file.FileContentService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<Resource> getThumbnail(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, FolderRequest request) throws IOException {
        try {

            FilePreviewResponse result = fileContentService.thumbnailFile(userInfo.getUserCode(), fileId, request.getFolderId());

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
    public ResponseEntity<Resource> previewFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, FolderRequest request) throws IOException {
        try {

            FilePreviewResponse result = fileContentService.previewFile(userInfo.getUserCode(), fileId, request.getFolderId());

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

    @GetMapping("/video/{fileId}")
    public ResponseEntity<Resource> previewVideo(
            @Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo,
            @PathVariable("fileId") String fileId,
            FolderRequest request
    ) {
        try {
            // 각 Range 요청도 Access Token으로 인증된다. 사용자와 폴더/파일 조건은 조회 쿼리에서 함께 검사한다.
            FilePreviewResponse result = fileContentService.previewFile(
                    userInfo.getUserCode(), fileId, request.getFolderId());
            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            // Resource를 200으로 반환하면 Spring MVC가 Range 요청을 206 부분 응답으로 처리한다.
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                    .contentType(result.getMediaType())
                    .body(result.getResource());
        } catch (Exception e) {
            log.error("Video preview failed. fileId={}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
