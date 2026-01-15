package org.nas.api.controller.v1.file;

import org.nas.api.model.v1.file.PreviewVo;
import org.nas.api.model.v1.file.response.PreviewResponse;
import org.nas.api.model.v1.folder.request.ActiveFolderRequest;
import org.springframework.core.io.Resource;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.file.response.FileResponse;
import org.nas.api.service.v1.file.FileService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "3.File", description = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseV1Controller {

    private final FileService fileService;

    @PostMapping("/list")
    public ResponseEntity<FileResponse> getFileList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody ActiveFolderRequest request) {
        try {

            List<File> fileList = fileService.getFileList(userInfo.getUserCode(), request.getActiveFolderId());

            return ResponseEntity.ok(FileResponse.getSuccess(fileList));
        } catch (Exception e) {
            return ResponseEntity.ok(FileResponse.getError());
        }
    }

    @GetMapping("/thumbnail/{fileId}")
    public ResponseEntity<Resource> getThumbnail(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, ActiveFolderRequest request) throws IOException {
        try {

            PreviewVo vo = fileService.getThumbnail(userInfo.getUserCode(), fileId, request.getActiveFolderId());

            if (vo == null || vo.getResource() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .contentType(vo.getMediaType())
                    .body(vo.getResource());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> getPreview(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @PathVariable("fileId") String fileId, ActiveFolderRequest request) throws IOException {
        try {

            PreviewVo vo = fileService.getPreview(userInfo.getUserCode(), fileId, request.getActiveFolderId());

            if (vo == null || vo.getResource() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .contentType(vo.getMediaType())
                    .body(vo.getResource());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
