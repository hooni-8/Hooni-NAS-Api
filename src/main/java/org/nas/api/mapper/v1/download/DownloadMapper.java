package org.nas.api.mapper.v1.download;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.download.FileDownload;

@Mapper
public interface DownloadMapper {

    FileDownload selectDownloadFileInfo(String userCode, String fileId, String folderId);

}
