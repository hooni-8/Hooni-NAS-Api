package org.nas.api.service.v1.folder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.folder.FolderMapper;
import org.nas.api.model.v1.file.File;
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

    public int createFolder(String userCode, String folderName, String folderId) {
        return folderMapper.createFolder(userCode, folderName, folderId);
    }

    public int deleteFolder(String userCode, String folderId) {

        List<Folder> deleteFolderList = folderMapper.deleteFolderList(userCode, folderId);

        try {
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
        } catch (Exception e) {
            return 0;
        }
    }
}
