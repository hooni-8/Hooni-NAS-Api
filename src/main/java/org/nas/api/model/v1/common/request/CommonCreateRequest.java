package org.nas.api.model.v1.common.request;

import lombok.Data;

@Data
public class CommonCreateRequest {

    private String groupId;

    private String commonCode;

    private String commonCodeName;
}
