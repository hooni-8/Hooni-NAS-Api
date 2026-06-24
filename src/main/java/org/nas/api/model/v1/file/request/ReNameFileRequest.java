package org.nas.api.model.v1.file.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReNameFileRequest {

    private String changeName;

    private String fileId;

    private String folderId;

}
