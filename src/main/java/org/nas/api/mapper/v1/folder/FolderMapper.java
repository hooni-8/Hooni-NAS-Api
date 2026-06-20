package org.nas.api.mapper.v1.folder;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.folder.Folder;

import java.util.List;

@Mapper
public interface FolderMapper {

    Folder rootFolder(String userCode);

    int createFolder(String userCode, String folderName, String folderId);
}
