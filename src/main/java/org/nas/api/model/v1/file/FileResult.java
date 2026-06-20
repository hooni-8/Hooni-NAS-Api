package org.nas.api.model.v1.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nas.api.common.code.StatusCode;
import org.nas.api.model.v1.folder.Folder;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResult {

    private String code;
    private String message;

    private List<File> file;
    private Folder folderInfo;

    public static FileResult getSuccses(Folder folderInfo, List<File> file) {
        return FileResult.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .folderInfo(folderInfo)
                .file(file)
                .build();
    }

    public static FileResult getError() {
        return FileResult.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .build();
    }
}
