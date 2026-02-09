package org.nas.api.service.v1.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nas.api.mapper.v1.file.FileMapper;
import org.nas.api.model.v1.file.DeleteFileVo;
import org.nas.api.model.v1.file.File;
import org.nas.api.model.v1.file.FileView;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;

    public List<File> getFileList(String userCode, String activeFolderId) {

        FileView fileView = FileView.builder()
                .userCode(userCode)
                .activeFolderId(activeFolderId)
                .build();

        return fileMapper.getFileList(fileView);

    }

    public int deleteFile(String userCode, String activeFolderId, String fileId) {

        DeleteFileVo vo = DeleteFileVo.builder()
                .userCode(userCode)
                .activeFolderId(activeFolderId)
                .fileId(fileId)
                .build();

        return fileMapper.deleteFile(vo);
    }

}
