package org.nas.api.model.v1.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.nio.file.Path;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TempUploadFile {

    private String originName;

    private Path tempPath;

    private long size;
}