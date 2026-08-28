package org.nas.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.file.FileMapper;
import org.nas.api.model.v1.file.DeleteFileVo;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.file.FileResult;
import org.nas.api.model.v1.file.FileView;
import org.nas.api.model.v1.folder.Folder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;

    public FileResult selectFileList(String userCode, String folderId) {

        FileView fileView = FileView.builder()
                .userCode(userCode)
                .folderId(folderId)
                .build();

        Folder folderInfo = fileMapper.selectFolderInfo(fileView);

        if (folderInfo == null) {
            // 존재 여부와 소유 여부를 구분하지 않아 다른 사용자의 폴더 정보를 노출하지 않는다.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다.");
        }

        List<File> file = fileMapper.selectFileList(fileView);

        return FileResult.builder()
                .folderInfo(folderInfo)
                .file(file)
                .build();

    }

    public int reNameFile(String userCode, String fileId, String changeName, String folderId) {
        return fileMapper.reNameFile(userCode, fileId, changeName, folderId);
    }

    public int deleteFile(String userCode, String folderId, String fileId) {

        DeleteFileVo vo = DeleteFileVo.builder()
                .userCode(userCode)
                .folderId(folderId)
                .fileId(fileId)
                .build();

        return fileMapper.deleteFile(vo);
    }

}
