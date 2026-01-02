package org.nas.api.service.v1.folder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.folder.FolderMapper;
import org.nas.api.model.v1.folder.Folder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderMapper folderMapper;

    public Folder rootFolder(String userCode) {
        return folderMapper.rootFolder(userCode);
    }

    public List<Folder> activeFolder(String userCode, String activeFolderCode) {
        return folderMapper.activeFolder(userCode, activeFolderCode);
    }

    public int createFolder(String userCode, String folderName, String activeFolderId) {
        return folderMapper.createFolder(userCode, folderName, activeFolderId);
    }
}
