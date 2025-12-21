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
public class FileUploadFile {

    private int fileId;

    private String jobId;

    private String originName;

    private String storedName;

    private String status;

    private String errorMessage;

    private Date createdAt;
}
