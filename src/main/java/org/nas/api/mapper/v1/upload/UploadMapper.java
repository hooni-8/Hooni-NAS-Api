package org.nas.api.mapper.v1.upload;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.upload.FileUpload;
import org.nas.api.model.v1.upload.PreviewUpload;

@Mapper
public interface UploadMapper {

    void upload(FileUpload fileUpload);

    void createPreview(PreviewUpload previewUpload);

}
