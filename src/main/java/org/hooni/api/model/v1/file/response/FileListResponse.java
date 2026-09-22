package org.hooni.api.model.v1.file.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hooni.api.model.v1.file.File;
import org.hooni.api.model.v1.folder.Folder;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileListResponse {

    private List<File> file;
    private Folder folderInfo;
}
