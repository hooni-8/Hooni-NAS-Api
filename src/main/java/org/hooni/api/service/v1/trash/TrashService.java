package org.hooni.api.service.v1.trash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.FilePreviewProvider.FilePreviewProvider;
import org.hooni.api.FilePreviewProvider.FilePreviewProviderResolver;
import org.hooni.api.common.code.FileTypeCode;
import org.hooni.api.mapper.v1.trash.TrashMapper;
import org.hooni.api.model.v1.file.ExpiredDeletedFile;
import org.hooni.api.model.v1.file.FilePreview;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.hooni.api.model.v1.trash.TrashItem;
import org.hooni.api.model.v1.trash.request.TrashListRequest;
import org.hooni.api.model.v1.trash.response.TrashListResponse;
import org.hooni.api.properties.DeletedFileCleanupProperties;
import org.hooni.api.properties.FilePathProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrashService {

    private final TrashMapper trashMapper;
    private final FilePathProperties filePathProperties;
    private final DeletedFileCleanupProperties cleanupProperties;
    private final FilePreviewProviderResolver previewProviderResolver;

    /**
     * 파일 하나를 휴지통으로 이동한다.
     * 파일 단위 삭제는 항상 새로운 TRASH_ID를 생성하며, 원본 파일과 미리보기는 함께 논리 삭제한다.
     */
    @Transactional
    public int moveFileToTrash(String userCode, String folderId, String fileId) {
        String trashId = UUID.randomUUID().toString();
        int inserted = trashMapper.insertFileTrashEntry(
                trashId,
                userCode,
                folderId,
                fileId,
                cleanupProperties.getRetentionDays()
        );

        if (inserted == 0) {
            return 0;
        }

        trashMapper.moveFilePreviewToTrash(fileId);
        int moved = trashMapper.moveFileToTrash(trashId, userCode, folderId, fileId);
        if (moved != 1) {
            throw new IllegalStateException("파일을 휴지통으로 이동하지 못했습니다.");
        }

        return moved;
    }

    /**
     * 폴더와 현재 활성 상태인 모든 하위 폴더·파일을 하나의 삭제 묶음으로 휴지통에 이동한다.
     * 이미 휴지통에 있던 하위 항목은 이번 TRASH_ID에 포함하지 않는다.
     */
    @Transactional
    public int moveFolderToTrash(String userCode, String folderId) {
        String trashId = UUID.randomUUID().toString();
        int inserted = trashMapper.insertFolderTrashEntry(
                trashId,
                userCode,
                folderId,
                cleanupProperties.getRetentionDays()
        );

        if (inserted == 0) {
            return 0;
        }

        trashMapper.moveFolderPreviewsToTrash(trashId, userCode, folderId);
        trashMapper.moveFolderFilesToTrash(trashId, userCode, folderId);
        int movedFolders = trashMapper.moveFolderTreeToTrash(trashId, userCode, folderId);
        if (movedFolders == 0) {
            throw new IllegalStateException("폴더를 휴지통으로 이동하지 못했습니다.");
        }

        return 1;
    }

    /**
     * 사용자의 휴지통 최상위 항목을 검색 조건과 페이지 기준으로 조회한다.
     * 폴더 내부 구성요소는 개별 행으로 노출하지 않고 해당 폴더의 TRASH_ID 한 건으로 반환한다.
     */
    @Transactional(readOnly = true)
    public TrashListResponse selectTrashList(String userCode, TrashListRequest request) {
        TrashListRequest normalizedRequest = request == null ? new TrashListRequest() : request;
        int page = normalizedRequest.getPage() == null ? 0 : normalizedRequest.getPage();
        int size = normalizedRequest.getSize() == null ? 50 : normalizedRequest.getSize();
        String keyword = normalizeKeyword(normalizedRequest.getKeyword());

        List<TrashItem> items = trashMapper.selectTrashItems(userCode, keyword, size, page * size);
        long totalCount = trashMapper.countTrashItems(userCode, keyword);

        return TrashListResponse.builder()
                .items(items)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .build();
    }

    /**
     * 휴지통에 보관 중인 이미지·영상의 기존 썸네일을 반환한다.
     * 소유권은 TRASH_ID와 사용자 코드를 함께 조회해 검증하고 저장소 기본 경로 밖의 파일은 열지 않는다.
     */
    @Transactional(readOnly = true)
    public FilePreviewResponse loadThumbnail(String userCode, String trashId) throws IOException {
        validateTrashId(trashId);

        FilePreview preview = trashMapper.selectTrashThumbnail(userCode, trashId);
        if (preview == null) {
            return null;
        }

        Path basePath = Paths.get(filePathProperties.getBasePath()).toAbsolutePath().normalize();
        Path thumbnailPath = basePath
                .resolve(preview.getStoragePath())
                .resolve(preview.getStoredName())
                .normalize();

        if (!thumbnailPath.startsWith(basePath) || !Files.isRegularFile(thumbnailPath)) {
            return null;
        }

        String contentType = Files.probeContentType(thumbnailPath);
        if (contentType == null) {
            return null;
        }

        FileTypeCode fileType = FileTypeCode.fromContentType(contentType);
        FilePreviewProvider provider = previewProviderResolver.resolve(fileType);
        return provider.preview(thumbnailPath);
    }

    /**
     * 선택한 TRASH_ID 묶음을 복원한다.
     * 원래 부모가 없어진 경우에만 루트 폴더로 이동하고, 부모도 함께 복원하는 경우에는 기존 위치를 유지한다.
     */
    @Transactional
    public void restore(String userCode, List<String> requestedTrashIds) {
        List<String> trashIds = normalizeTrashIds(requestedTrashIds);
        validateOwnership(userCode, trashIds);

        // 원래 상위 폴더가 이미 영구 삭제된 항목만 사용자 루트 폴더로 복원한다.
        trashMapper.moveOrphanFileRootsToUserRoot(userCode, trashIds);
        trashMapper.moveOrphanFolderRootsToUserRoot(userCode, trashIds);

        // 미리보기와 폴더를 먼저 복원한 뒤 파일을 활성화하고 마지막에 휴지통 이력을 제거한다.
        trashMapper.restorePreviews(userCode, trashIds);
        trashMapper.restoreFolders(userCode, trashIds);
        trashMapper.restoreFiles(userCode, trashIds);

        int deletedEntries = trashMapper.deleteTrashEntries(userCode, trashIds);
        if (deletedEntries != trashIds.size()) {
            throw new IllegalStateException("휴지통 복원 상태를 정리하지 못했습니다.");
        }
    }

    /** 선택한 휴지통 항목의 실제 저장 파일과 DB 메타데이터를 영구 삭제한다. */
    @Transactional
    public void purge(String userCode, List<String> requestedTrashIds) {
        List<String> trashIds = normalizeTrashIds(requestedTrashIds);
        validateOwnership(userCode, trashIds);
        purgeTrashEntries(userCode, trashIds);
    }

    /** 해당 사용자의 ACTIVE 휴지통 항목을 모두 영구 삭제한다. */
    @Transactional
    public void empty(String userCode) {
        List<String> trashIds = trashMapper.selectActiveTrashIdsByUser(userCode);
        if (!trashIds.isEmpty()) {
            purgeTrashEntries(userCode, trashIds);
        }
    }

    /** 보관 만료 시각이 지난 휴지통 항목을 설정된 배치 크기만큼 정리한다. */
    @Transactional
    public void purgeExpiredTrashEntries() {
        List<String> trashIds = trashMapper.selectExpiredTrashIds(cleanupProperties.getBatchSize());
        if (!trashIds.isEmpty()) {
            purgeTrashEntries(null, trashIds);
        }
    }

    private void purgeTrashEntries(String userCode, List<String> trashIds) {
        // ACTIVE -> PURGING 전환에 성공한 요청만 처리하여 복원/삭제 동시 실행을 차단한다.
        int claimedEntries = trashMapper.markTrashEntriesPurging(userCode, trashIds);
        if (claimedEntries != trashIds.size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 요청에서 처리 중인 휴지통 항목이 있습니다.");
        }

        // 디스크 파일을 먼저 제거한다. 실패하면 예외를 발생시켜 DB 변경을 롤백하고 다음 정리에서 재시도한다.
        List<ExpiredDeletedFile> files = trashMapper.selectFilesForPurge(userCode, trashIds);
        for (ExpiredDeletedFile file : files) {
            deleteStoredFiles(file);
        }

        trashMapper.deletePreviewMetadataByTrashIds(userCode, trashIds);
        trashMapper.deleteFileMetadataByTrashIds(userCode, trashIds);

        // 자기 참조 폴더 FK를 지키기 위해 자식이 없는 리프 폴더부터 반복 삭제한다.
        int deletedFolders;
        do {
            deletedFolders = trashMapper.deleteLeafFoldersByTrashIds(userCode, trashIds);
        } while (deletedFolders > 0);

        // 다른 휴지통 항목이 아직 자식으로 남아 있으면 폴더 tombstone은 유지한다.
        trashMapper.detachRemainingFoldersFromTrash(userCode, trashIds);

        int deletedEntries = trashMapper.deleteTrashEntries(userCode, trashIds);
        if (deletedEntries != trashIds.size()) {
            throw new IllegalStateException("휴지통 항목을 영구 삭제하지 못했습니다.");
        }
    }

    private void validateOwnership(String userCode, List<String> trashIds) {
        int ownedCount = trashMapper.countOwnedActiveTrashEntries(userCode, trashIds);
        if (ownedCount != trashIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "휴지통 항목을 찾을 수 없습니다.");
        }
    }

    private List<String> normalizeTrashIds(List<String> requestedTrashIds) {
        if (requestedTrashIds == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "휴지통 항목을 선택해주세요.");
        }

        List<String> trashIds = requestedTrashIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .distinct()
                .toList();

        if (trashIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "휴지통 항목을 선택해주세요.");
        }

        for (String trashId : trashIds) {
            try {
                UUID.fromString(trashId);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 휴지통 ID입니다.");
            }
        }

        return trashIds;
    }

    private void validateTrashId(String trashId) {
        try {
            UUID.fromString(trashId);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 휴지통 ID입니다.");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private void deleteStoredFiles(ExpiredDeletedFile file) {
        try {
            deleteIfPresent(file.getStoragePath(), file.getStoredName());
            deleteIfPresent(file.getPreviewStoragePath(), file.getPreviewStoredName());
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("저장소의 파일을 삭제하지 못했습니다. fileId=" + file.getFileId(), e);
        }
    }

    private void deleteIfPresent(String storagePath, String storedName) throws IOException {
        if (storagePath == null || storedName == null) {
            return;
        }

        Path basePath = Paths.get(filePathProperties.getBasePath()).toAbsolutePath().normalize();
        Path targetPath = basePath.resolve(storagePath).resolve(storedName).normalize();

        if (!targetPath.startsWith(basePath)) {
            throw new IllegalArgumentException("저장소 기본 경로 밖의 파일은 삭제할 수 없습니다.");
        }

        Files.deleteIfExists(targetPath);
    }

}
