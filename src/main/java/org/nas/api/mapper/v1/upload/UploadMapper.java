package org.nas.api.mapper.v1.upload;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.upload.FileUpload;

@Mapper
public interface UploadMapper {

    void upload(FileUpload fileUpload);
}
