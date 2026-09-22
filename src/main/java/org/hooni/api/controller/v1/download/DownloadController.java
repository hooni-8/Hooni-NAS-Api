package org.hooni.api.controller.v1.download;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.download.request.SingleDownloadRequest;
import org.hooni.api.service.v1.download.DownloadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@Tag(name = "4.Download", description = "Download")
@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController extends BaseV1Controller {

    private final DownloadService downloadService;

    @PostMapping("/single")
    public ResponseEntity<Resource> downloadSingle(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, SingleDownloadRequest request) throws IOException {
        try {
             return downloadService.downloadSingle(userInfo.getUserCode(), request.getFileId(), request.getFolderId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
