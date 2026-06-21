package org.nas.api.mapper.v1.folder;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.folder.Folder;

import java.util.List;

@Mapper
public interface FolderMapper {

    Folder rootFolder(String userCode);

    int createFolder(String userCode, String folderName, String folderId);

    List<Folder> deleteFolderList(String userCode, String folderId);

    List<File> deleteFileList(String userCode, String folderId);

    int deleteFile(String userCode, String fileId, String folderId);

    int deleteFolder(String userCode, String parentFolderId, String folderId);

    int deletePreview(String fileId);
}
