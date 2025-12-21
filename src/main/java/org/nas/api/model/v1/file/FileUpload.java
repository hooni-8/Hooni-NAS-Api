package org.nas.api.model.v1.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUpload {

    private int id;

    private String fileId;

    private String folderId;

    private String userCode;

    private String originName;

    private String storedName;

    private String extension;

    private long fileSize;

    private String storagePath;
}
