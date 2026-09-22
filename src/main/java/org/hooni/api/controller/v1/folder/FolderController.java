package org.hooni.api.controller.v1.folder;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.folder.Folder;
import org.hooni.api.model.v1.folder.request.CreateFolderRequest;
import org.hooni.api.model.v1.folder.request.DeleteFolderRequest;
import org.hooni.api.model.v1.folder.request.ReNameFolderRequest;
import org.hooni.api.service.v1.folder.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public void createFolder(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody CreateFolderRequest request) {
        ensureUpdated(folderService.createFolder(userInfo.getUserCode(), request.getFolderName(), request.getFolderId()));
    }

    @PostMapping("/rename")
    public void reNameFolder(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody ReNameFolderRequest request) {
        ensureUpdated(folderService.reNameFolder(userInfo.getUserCode(), request.getFolderId(), request.getChangeName(), request.getParentFolderId()));
    }

    @PostMapping("/delete")
    public void deleteFolder(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @RequestBody DeleteFolderRequest request) {
        ensureUpdated(folderService.deleteFolder(userInfo.getUserCode(), request.getFolderId()));
    }

    private void ensureUpdated(int result) {
        if (result <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 폴더를 찾을 수 없습니다.");
        }
    }
}
