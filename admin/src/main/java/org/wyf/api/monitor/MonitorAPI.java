package org.wyf.api.monitor;

import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.service.MonitorService;
import org.wyf.dto.monitor.MonitorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.service.MonitorService;

@RestController
@RequestMapping("monitor")
public class MonitorAPI {
    @Autowired
    private MonitorService monitorService;

    @PostMapping("getPileSatusList")
    public ResultDTO getPileSatusList(@RequestBody RequestDTO<MonitorDTO> requestDTO){



        return monitorService.getPileSatusList(requestDTO.getBody());
    }

}
