package org.wyf.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DeviceStatusVO {

    private String deviceNo;
    private Integer status;
    private List<Integer> plugs;

}
