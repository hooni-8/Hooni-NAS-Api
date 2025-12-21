package org.nas.api.common.code;

import lombok.Getter;

@Getter
public enum JobStatusCode {
    WAIT("WAIT")
    , SUCCESS("SUCCESS")
    , UPLOADING("UPLOADING")
    , DONE("DONE")
    , FAIL("FAIL");

    private final String code;

    JobStatusCode(String code) {
        this.code = code;
    }
}
