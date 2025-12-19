package org.admin.profile.model.v1.group.request;

import lombok.Data;

@Data
public class GroupCreateRequest {

    private String groupCode;

    private String groupName;
}
