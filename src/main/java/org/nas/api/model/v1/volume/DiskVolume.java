package org.nas.api.model.v1.volume;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiskVolume {

    private long total;

    private long free;

    private long usable;
}
