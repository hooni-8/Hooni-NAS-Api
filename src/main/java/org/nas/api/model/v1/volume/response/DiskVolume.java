package org.nas.api.model.v1.volume.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiskVolume {

    long total;

    long free;

    long usable;

    double usedPercent;
}
