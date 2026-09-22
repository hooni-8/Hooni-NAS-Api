package org.hooni.api.service.v1.folder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.mapper.v1.folder.FolderMapper;
import org.hooni.api.model.v1.file.File;
import org.hooni.api.model.v1.folder.Folder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderMapper folderMapper;

    public Folder rootFolder(String userCode) {
        return folderMapper.rootFolder(userCode);
    }

    public int createFolder(String userCode, String folderName, String folderId) {
        return folderMapper.createFolder(userCode, folderName, folderId);
    }

    public int reNameFolder(String userCode, String folderId, String changeName, String parentFolderId) {
        return folderMapper.reNameFolder(userCode, folderId, changeName, parentFolderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteFolder(String userCode, String folderId) {

        List<Folder> deleteFolderList = folderMapper.deleteFolderList(userCode, folderId);

        if (deleteFolderList.isEmpty()) {
            return 0;
        }

        for (Folder folder : deleteFolderList) {
            List<File> deleteFileList = folderMapper.deleteFileList(userCode, folder.getFolderId());

            if (!deleteFileList.isEmpty()) {
                for (File file : deleteFileList) {
                    folderMapper.deleteFile(userCode, file.getFileId(), folder.getFolderId());

                    folderMapper.deletePreview(file.getFileId());
                }
            }

            folderMapper.deleteFolder(userCode, folder.getParentFolderId(), folder.getFolderId());
        }

        return 1;
    }


}
