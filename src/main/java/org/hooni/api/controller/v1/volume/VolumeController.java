package org.hooni.api.controller.v1.volume;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hooni.api.controller.v1.BaseV1Controller;
import org.hooni.api.model.v1.volume.DiskVolume;
import org.hooni.api.service.v1.volume.VolumeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "1.Volume", description = "Volume")
@RestController
@RequestMapping("/volume")
@RequiredArgsConstructor
public class VolumeController extends BaseV1Controller {

    private final VolumeService volumeService;

    @PostMapping("/disk")
    public DiskVolume diskVolume() {
        return volumeService.diskVolume();
    }
}
