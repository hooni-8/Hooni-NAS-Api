package org.nas.api.controller.v1.file;

import org.nas.api.model.v1.code.response.CodeResult;
import org.nas.api.model.v1.file.request.DeleteFileInVo;
import org.nas.api.model.v1.folder.request.ActiveFolderRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.file.File;
import org.nas.api.service.v1.file.FileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "3.File", description = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseV1Controller {

    private final FileService fileService;

    @PostMapping("/list")
    public List<File> getFileList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody ActiveFolderRequest request) {
        return fileService.getFileList(userInfo.getUserCode(), request.getActiveFolderId());
    }

    @PostMapping("/delete")
    public int deleteFile(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody DeleteFileInVo request) {
        return fileService.deleteFile(userInfo.getUserCode(), request.getActiveFolderId(), request.getFileId());
    }

}
