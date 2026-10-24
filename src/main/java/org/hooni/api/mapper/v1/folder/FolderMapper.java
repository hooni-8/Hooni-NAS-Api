package org.hooni.api.mapper.v1.folder;

import org.apache.ibatis.annotations.Mapper;
import org.hooni.api.model.v1.folder.Folder;

@Mapper
public interface FolderMapper {

    Folder rootFolder(String userCode);

    int createFolder(String userCode, String folderName, String folderId);

    int reNameFolder(String userCode, String folderId, String changeName, String parentFolderId);

}
