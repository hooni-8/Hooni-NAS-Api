package org.hooni.api.service.v1.folder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.mapper.v1.folder.FolderMapper;
import org.hooni.api.model.v1.folder.Folder;
import org.hooni.api.service.v1.trash.TrashService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderMapper folderMapper;
    private final TrashService trashService;

    public Folder rootFolder(String userCode) {
        return folderMapper.rootFolder(userCode);
    }

    public int createFolder(String userCode, String folderName, String folderId) {
        return folderMapper.createFolder(userCode, folderName, folderId);
    }

    public int reNameFolder(String userCode, String folderId, String changeName, String parentFolderId) {
        return folderMapper.reNameFolder(userCode, folderId, changeName, parentFolderId);
    }

    public int deleteFolder(String userCode, String folderId) {
        return trashService.moveFolderToTrash(userCode, folderId);
    }


}
