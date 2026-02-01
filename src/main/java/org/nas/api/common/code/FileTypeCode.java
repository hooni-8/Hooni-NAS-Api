package org.nas.api.common.code;

public enum FileTypeCode {
    IMAGE
    , VIDEO
    , OTHER;

    public static FileTypeCode fromContentType(String contentType) {
        if (contentType == null) return OTHER;

        if (contentType.startsWith("image/")) return IMAGE;
        if (contentType.startsWith("video/")) return VIDEO;

        return OTHER;
    }
}
