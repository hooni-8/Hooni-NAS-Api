package org.hooni.api.mapper.v1.download;

import org.apache.ibatis.annotations.Mapper;
import org.hooni.api.model.v1.download.FileDownload;

@Mapper
public interface DownloadMapper {

    FileDownload selectDownloadFileInfo(String userCode, String fileId, String folderId);

}
