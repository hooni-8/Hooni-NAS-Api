package org.hooni.api.mapper.v1.file;

import org.apache.ibatis.annotations.Mapper;
import org.hooni.api.model.v1.file.DeleteFile;
import org.hooni.api.model.v1.file.File;
import org.hooni.api.model.v1.file.FilePreview;
import org.hooni.api.model.v1.file.FileView;
import org.hooni.api.model.v1.folder.Folder;

import java.util.List;

@Mapper
public interface FileMapper {

    Folder selectFolderInfo(FileView fileView);

    List<File> selectFileList(FileView fileView);

    FilePreview getThumbnail(FileView fileView);

    FilePreview getPreview(FileView fileView);

    int deleteFile(DeleteFile deleteFileVo);

    int reNameFile(String userCode, String fileId, String changeName, String folderId);

    int deletePreview(DeleteFile deleteFileVo);
}
