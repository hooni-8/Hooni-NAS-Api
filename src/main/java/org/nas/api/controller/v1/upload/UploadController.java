package org.nas.api.controller.v1.upload;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.upload.TempUploadFile;
import org.nas.api.model.v1.upload.request.UploadRequest;
import org.nas.api.service.v1.upload.UploadService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "2.Upload", description = "Upload")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController extends BaseV1Controller {

    private final UploadService uploadService;

    @PostMapping("/progress")
    public void upload(
            @Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo
            , @RequestPart(value = "files", required = false) MultipartFile file
            , @RequestPart(value = "request", required = false) UploadRequest request) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        if (request == null || request.getFolderId() == null || request.getFolderId().isBlank()) {
            throw new IllegalArgumentException("업로드할 폴더 정보가 없습니다.");
        }

        TempUploadFile tempUploadFile = uploadService.createFile(file);
        // 실제 파일 이동 및 DB 저장이 모두 끝난 경우에만 성공으로 응답한다.
        uploadService.upload(userInfo.getUserCode(), request.getFolderId(), request.getLastModifiedAt(), tempUploadFile);

    }


}
