package org.nas.api.common;

import org.nas.api.model.v1.common.CommonCreate;
import org.nas.api.model.v1.common.CommonSelect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonMapper {

    void commonCodeCreate(CommonCreate commonCreate);

    List<CommonSelect> commonCodeSelect(String groupId);
}
