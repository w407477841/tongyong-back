package org.wyf.vo.monitor;

import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.vo.DeviceStatusVO;
import lombok.Data;
import org.wyf.cache.dto.DevicePlusStatusDTO;

import java.util.List;

@Data
public class MonitorVO {
    private String pileName;
    private String stationName;
    private List<DevicePlusStatusDTO> deviceStatusList;
    private Integer online;


}
