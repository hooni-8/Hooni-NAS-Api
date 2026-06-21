package org.nas.api.controller.v1.folder;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.controller.v1.BaseV1Controller;
import org.nas.api.model.v1.code.response.CodeResult;
import org.nas.api.model.v1.folder.Folder;
import org.nas.api.model.v1.folder.request.CreateFolderRequest;
import org.nas.api.model.v1.folder.request.DeleteFolderRequest;
import org.nas.api.service.v1.folder.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "3.Folder", description = "Folder")
@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController extends BaseV1Controller {

    private final FolderService folderService;

    @PostMapping("/root")
    public Folder rootFolder(@AuthenticationPrincipal DefaultUserInfo userInfo) {
        return folderService.rootFolder(userInfo.getUserCode());
    }

    @PostMapping("/create")
    public ResponseEntity<CodeResult> createFolder(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody CreateFolderRequest request) {
        try {
            int result = folderService.createFolder(userInfo.getUserCode(), request.getFolderName(), request.getFolderId());

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
    public ResponseEntity<CodeResult> deleteFolder(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody DeleteFolderRequest request) {
        try {
            int result = folderService.deleteFolder(userInfo.getUserCode(), request.getFolderId());

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
}
