package org.nas.api.common;

import org.nas.api.model.v1.group.GroupCreate;
import org.nas.api.model.v1.group.GroupSelect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {

    void groupCodeCreate(GroupCreate groupCreate);

    List<GroupSelect> groupCodeSelect();

}
