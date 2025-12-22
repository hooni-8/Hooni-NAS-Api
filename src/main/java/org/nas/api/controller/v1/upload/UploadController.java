package org.nas.api.controller.v1.upload;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.upload.TempUploadFile;
import org.nas.api.model.v1.upload.request.UploadRequest;
import org.nas.api.model.v1.upload.response.UploadResponse;
import org.nas.api.service.v1.upload.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Tag(name = "2.Upload", description = "Upload")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController extends BaseV1Controller {

    private final UploadService uploadService;

    @PostMapping("/progress")
    public ResponseEntity<UploadResponse> upload(
            @Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo
            , @RequestPart(value = "files", required = false) MultipartFile file
            , @RequestPart(value = "request", required = false) UploadRequest request) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        try {
            // 임시파일 생성
            TempUploadFile tempUploadFileList = uploadService.createFile(file);

            uploadService.uploadAsync(userInfo.getUserCode(), request.getFolderId(), request.getLastModifiedAt(), tempUploadFileList);

            return ResponseEntity.ok(UploadResponse.getSuccess());
        } catch (Exception e) {
            return ResponseEntity.ok(UploadResponse.getError());
        }
    }


}
