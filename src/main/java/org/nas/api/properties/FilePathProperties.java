package org.nas.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file.path")
public class FilePathProperties {

    private String basePath;

    private String basePathDev;

    private String tempBasePath;

    private String thumbnailBasePath;
}
