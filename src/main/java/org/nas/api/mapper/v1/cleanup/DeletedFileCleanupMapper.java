package org.nas.api.mapper.v1.cleanup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nas.api.model.v1.file.ExpiredDeletedFile;

import java.util.List;

@Mapper
public interface DeletedFileCleanupMapper {

    List<ExpiredDeletedFile> selectExpiredDeletedFiles(
            @Param("retentionDays") int retentionDays,
            @Param("limit") int limit
    );

    int deletePreviewMetadata(@Param("fileId") String fileId);

    int deleteExpiredFileMetadata(
            @Param("fileId") String fileId,
            @Param("retentionDays") int retentionDays
    );

    int deleteExpiredEmptyFolders(@Param("retentionDays") int retentionDays);
}
