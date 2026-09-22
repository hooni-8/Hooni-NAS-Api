package org.hooni.api.controller.v1.file;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.file.request.DeleteFileInRequest;
import org.hooni.api.model.v1.file.request.ReNameFileRequest;
import org.hooni.api.model.v1.file.response.FileListResponse;
import org.hooni.api.model.v1.folder.request.FolderRequest;
import org.hooni.api.service.v1.file.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "3.File", description = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseV1Controller {

    private final FileService fileService;

    @PostMapping("/list")
    public FileListResponse selectFileList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody FolderRequest request) {
        return fileService.selectFileList(userInfo.getUserCode(), request.getFolderId());
    }

    @PostMapping("/rename")
    public void reNameFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody ReNameFileRequest request) {
        ensureUpdated(fileService.reNameFile(userInfo.getUserCode(), request.getFileId(), request.getChangeName(), request.getFolderId()));
    }

    @PostMapping("/delete")
    public void deleteFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody DeleteFileInRequest request) {
        ensureUpdated(fileService.deleteFile(userInfo.getUserCode(), request.getFolderId(), request.getFileId()));
    }

    private void ensureUpdated(int result) {
        if (result <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 파일을 찾을 수 없습니다.");
        }
    }

}
