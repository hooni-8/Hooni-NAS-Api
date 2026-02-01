package org.nas.api.common.utils;

import java.util.Set;

public final class FileUtils {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "mov", "avi", "mkv", "webm", "wmv", "flv");

    // 파일 확장자 추출
    public static String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf(".");

        return (idx == -1) ? "" : filename.substring(idx).toLowerCase();
    }

    public static boolean isImage(String extension) {
        return IMAGE_EXTENSIONS.contains(normalize(extension));
    }

    public static boolean isImageFile(String fileName) {
        return isFileType(fileName, IMAGE_EXTENSIONS);
    }

    /* ================= 영상 ================= */

    public static boolean isVideo(String extension) {
        return VIDEO_EXTENSIONS.contains(normalize(extension));
    }

    public static boolean isVideoFile(String fileName) {
        return isFileType(fileName, VIDEO_EXTENSIONS);
    }

    /* ================= 공통 ================= */

    private static boolean isFileType(String fileName, Set<String> extensions) {
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        return extensions.contains(normalize(ext));
    }

    private static String normalize(String extension) {
        if (extension == null) {
            return "";
        }

        String ext = extension.trim().toLowerCase();
        return ext.startsWith(".") ? ext.substring(1) : ext;
    }

}
