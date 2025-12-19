package org.admin.profile.model.v1.group;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupCreate {

    private String groupCode;

    private String groupName;

    private String userCode;
}
