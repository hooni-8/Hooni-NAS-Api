package org.admin.profile.mapper.v1.common;

import org.admin.profile.model.v1.group.GroupCreate;
import org.admin.profile.model.v1.group.GroupSelect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {

    void groupCodeCreate(GroupCreate groupCreate);

    List<GroupSelect> groupCodeSelect();

}
