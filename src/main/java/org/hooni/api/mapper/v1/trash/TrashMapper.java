package org.hooni.api.mapper.v1.trash;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.hooni.api.model.v1.file.ExpiredDeletedFile;
import org.hooni.api.model.v1.file.FilePreview;
import org.hooni.api.model.v1.trash.TrashItem;

import java.util.List;

@Mapper
public interface TrashMapper {

    /** 파일 삭제 이력 한 건을 생성한다. */
    int insertFileTrashEntry(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId,
            @Param("fileId") String fileId,
            @Param("retentionDays") int retentionDays
    );

    /** 폴더 삭제 이력 한 건을 생성한다. */
    int insertFolderTrashEntry(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId,
            @Param("retentionDays") int retentionDays
    );

    /** 단일 파일의 미리보기를 논리 삭제한다. */
    int moveFilePreviewToTrash(@Param("fileId") String fileId);

    /** 단일 파일에 TRASH_ID를 지정하고 논리 삭제한다. */
    int moveFileToTrash(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId,
            @Param("fileId") String fileId
    );

    /** 폴더 트리에 포함된 활성 파일의 미리보기를 논리 삭제한다. */
    int moveFolderPreviewsToTrash(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId
    );

    /** 폴더 트리에 포함된 활성 파일에 동일한 TRASH_ID를 지정한다. */
    int moveFolderFilesToTrash(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId
    );

    /** 폴더 트리 전체에 동일한 TRASH_ID를 지정하고 논리 삭제한다. */
    int moveFolderTreeToTrash(
            @Param("trashId") String trashId,
            @Param("userCode") String userCode,
            @Param("folderId") String folderId
    );

    /** 휴지통의 최상위 삭제 항목을 조회한다. */
    List<TrashItem> selectTrashItems(
            @Param("userCode") String userCode,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /** 휴지통 목록의 검색 조건에 해당하는 전체 건수를 조회한다. */
    long countTrashItems(
            @Param("userCode") String userCode,
            @Param("keyword") String keyword
    );

    /** 로그인 사용자가 휴지통에 보관 중인 파일의 썸네일 정보를 조회한다. */
    FilePreview selectTrashThumbnail(
            @Param("userCode") String userCode,
            @Param("trashId") String trashId
    );

    /** 요청한 TRASH_ID가 모두 해당 사용자의 ACTIVE 항목인지 확인한다. */
    int countOwnedActiveTrashEntries(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** 원래 부모가 없는 파일 루트 항목을 사용자 루트 폴더로 옮긴다. */
    int moveOrphanFileRootsToUserRoot(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** 원래 부모가 없는 폴더 루트 항목을 사용자 루트 폴더로 옮긴다. */
    int moveOrphanFolderRootsToUserRoot(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** 복원 대상 파일의 미리보기를 활성화한다. */
    int restorePreviews(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** 복원 대상 폴더의 삭제 상태와 TRASH_ID를 해제한다. */
    int restoreFolders(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** 복원 대상 파일의 삭제 상태와 TRASH_ID를 해제한다. */
    int restoreFiles(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    List<String> selectActiveTrashIdsByUser(@Param("userCode") String userCode);

    List<String> selectExpiredTrashIds(@Param("limit") int limit);

    List<ExpiredDeletedFile> selectFilesForPurge(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    /** ACTIVE 항목을 PURGING으로 원자적으로 전환하여 처리 권한을 선점한다. */
    int markTrashEntriesPurging(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    int deletePreviewMetadataByTrashIds(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    int deleteFileMetadataByTrashIds(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    int deleteLeafFoldersByTrashIds(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    int detachRemainingFoldersFromTrash(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );

    int deleteTrashEntries(
            @Param("userCode") String userCode,
            @Param("trashIds") List<String> trashIds
    );
}
