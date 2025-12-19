package org.admin.profile.mapper.v1.common;

import org.admin.profile.model.v1.common.CommonCreate;
import org.admin.profile.model.v1.common.CommonSelect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonMapper {

    void commonCodeCreate(CommonCreate commonCreate);

    List<CommonSelect> commonCodeSelect(String groupId);
}
