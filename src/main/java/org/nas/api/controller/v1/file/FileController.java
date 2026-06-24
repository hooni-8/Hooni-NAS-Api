package org.nas.api.controller.v1.file;

import org.nas.api.model.v1.code.response.CodeResult;
import org.nas.api.model.v1.file.FileResult;
import org.nas.api.model.v1.file.request.DeleteFileInVo;
import org.nas.api.model.v1.file.request.ReNameFileRequest;
import org.nas.api.model.v1.folder.request.FolderRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.service.v1.file.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "3.File", description = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseV1Controller {

    private final FileService fileService;

    @PostMapping("/list")
    public FileResult selectFileList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody FolderRequest request) {
        return fileService.selectFileList(userInfo.getUserCode(), request.getFolderId());
    }

    @PostMapping("/rename")
    public ResponseEntity<CodeResult> reNameFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody ReNameFileRequest request) {
        try {

            int result = fileService.reNameFile(userInfo.getUserCode(), request.getFileId(), request.getChangeName(), request.getFolderId());

            if (result > 0) {
                return ResponseEntity.ok(CodeResult.getSuccess());
            } else {
                return ResponseEntity.ok(CodeResult.getError());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok(CodeResult.getError());
        }
    }

    @PostMapping("/delete")
    public int deleteFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody DeleteFileInVo request) {
        return fileService.deleteFile(userInfo.getUserCode(), request.getFolderId(), request.getFileId());
    }

}
