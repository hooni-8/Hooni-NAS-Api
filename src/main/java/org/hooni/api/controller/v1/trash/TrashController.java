package org.hooni.api.controller.v1.trash;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.hooni.api.model.v1.trash.request.TrashActionRequest;
import org.hooni.api.model.v1.trash.request.TrashListRequest;
import org.hooni.api.model.v1.trash.response.TrashListResponse;
import org.hooni.api.service.v1.trash.TrashService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@Tag(name = "6.Trash", description = "Trash")
@RestController
@RequestMapping("/trash")
@RequiredArgsConstructor
public class TrashController extends BaseV1Controller {

    private final TrashService trashService;

    /** 휴지통 최상위 항목을 검색·페이징하여 조회한다. */
    @PostMapping("/list")
    public TrashListResponse selectTrashList(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @Valid @RequestBody(required = false) TrashListRequest request) {
        return trashService.selectTrashList(userInfo.getUserCode(), request);
    }

    /** 휴지통 보관 기간 중인 파일의 썸네일을 반환한다. */
    @GetMapping("/thumbnail/{trashId}")
    public ResponseEntity<Resource> getThumbnail(
            @Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo,
            @PathVariable("trashId") String trashId
    ) throws IOException {
        try {
            FilePreviewResponse result = trashService.loadThumbnail(userInfo.getUserCode(), trashId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            // 삭제 파일은 민감할 수 있으므로 브라우저 캐시에 남기지 않는다.
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .contentType(result.getMediaType())
                    .body(result.getResource());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Trash thumbnail load failed. trashId={}", trashId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** 선택한 TRASH_ID 묶음을 원래 위치 또는 사용자 루트 폴더로 복원한다. */
    @PostMapping("/restore")
    public void restore(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @Valid @RequestBody TrashActionRequest request) {
        trashService.restore(userInfo.getUserCode(), request.getTrashIds());
    }

    /** 선택한 TRASH_ID 묶음의 실제 파일과 메타데이터를 영구 삭제한다. */
    @PostMapping("/purge")
    public void purge(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo, @Valid @RequestBody TrashActionRequest request) {
        trashService.purge(userInfo.getUserCode(), request.getTrashIds());
    }

    /** 로그인 사용자의 휴지통을 모두 비운다. */
    @PostMapping("/empty")
    public void empty(@Parameter(hidden = true) @AuthenticationPrincipal DefaultUserInfo userInfo) {
        trashService.empty(userInfo.getUserCode());
    }

}
