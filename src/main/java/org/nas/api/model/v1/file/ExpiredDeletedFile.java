package org.nas.api.model.v1.file;

import lombok.Data;

@Data
public class ExpiredDeletedFile {

    private String fileId;
    private String storagePath;
    private String storedName;
    private String previewStoragePath;
    private String previewStoredName;
}
