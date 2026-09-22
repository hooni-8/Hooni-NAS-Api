package org.hooni.api.mapper.v1.upload;

import org.apache.ibatis.annotations.Mapper;
import org.hooni.api.model.v1.upload.FileUpload;
import org.hooni.api.model.v1.upload.PreviewUpload;

@Mapper
public interface UploadMapper {

    void upload(FileUpload fileUpload);

    void createPreview(PreviewUpload previewUpload);

}
