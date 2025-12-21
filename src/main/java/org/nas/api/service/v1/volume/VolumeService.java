package org.nas.api.service.v1.volume;

import lombok.extern.slf4j.Slf4j;
import org.nas.api.model.v1.volume.DiskVolume;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class VolumeService {

    public DiskVolume diskVolume() {
        String os = System.getProperty("os.name").toLowerCase();

        File root;
        if (os.contains("windows")) {
            root = new File("C:\\");
        } else {
            root = new File("/attachfile");
        }

        long total = root.getTotalSpace();      // 전체 용량
        long free = root.getFreeSpace();        // 남은 용량
        long usable = root.getUsableSpace();    // 사용 가능한 용량

        return DiskVolume.builder()
                .total(total)
                .free(free)
                .usable(usable)
                .build();
    }
}
