package org.nas.api.model.v1.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadJob {
    private String jobId;

    private String userCode;

    private String folderId;

    private int totalCount;

    private int doneCount;

    private int failCount;

    private String status;

    private Date createdAt;

    private Date updatedAt;
}
