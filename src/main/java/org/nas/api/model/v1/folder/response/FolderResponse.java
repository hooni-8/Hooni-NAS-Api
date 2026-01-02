package org.nas.api.model.v1.folder.response;

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
public class FolderResponse {

    private String code;

    private String message;

    private Folder data;

    private List<Folder> dataList;

    public static FolderResponse getSuccess(Folder folder) {
        return FolderResponse.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(folder)
                .build();
    }

    public static FolderResponse getListSuccess(List<Folder> folderList) {
        return FolderResponse.builder()
                .code(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .dataList(folderList)
                .build();
    }



    public static FolderResponse getError() {
        return FolderResponse.builder()
                .code(StatusCode.ERROR.getCode())
                .message(StatusCode.ERROR.getMessage())
                .build();
    }
}
