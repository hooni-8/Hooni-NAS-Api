package org.admin.profile.model.v1.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonCreate {

    private String groupId;

    private String commonCode;

    private String commonCodeName;

    private String userCode;
}
