package org.nas.api.mapper.v1.file;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.file.FileUpload;

@Mapper
public interface FileMapper {

    void upload(FileUpload fileUpload);
}
