package org.nas.api.controller.v1.file;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.file.response.FileResponse;
import org.nas.api.service.v1.file.FileService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<FileResponse> selectFileList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo) {
        try {

            List<File> fileList = fileService.selectFileList(userInfo.getUserCode());
            log.info("fileList => {}", fileList.toString());
            return ResponseEntity.ok(FileResponse.getSuccess(fileList));
        } catch (Exception e) {
            return ResponseEntity.ok(FileResponse.getError());
        }
    }

}
