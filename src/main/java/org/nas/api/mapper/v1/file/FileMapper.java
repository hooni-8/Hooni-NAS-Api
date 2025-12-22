package org.nas.api.mapper.v1.file;

import org.apache.ibatis.annotations.Mapper;
import org.nas.api.model.v1.file.File;

import java.util.List;

@Mapper
public interface FileMapper {

    List<File> selectFileList(String userCode);
}
