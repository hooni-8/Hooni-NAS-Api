package org.hooni.api.common.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefaultUserInfo {

    private final String userName;
    private final String userCode;
    private final String role;
}
