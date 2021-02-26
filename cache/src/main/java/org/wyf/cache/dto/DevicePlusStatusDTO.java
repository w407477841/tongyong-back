package org.wyf.cache.dto;

import lombok.Data;

@Data
public class DevicePlusStatusDTO {
    /**
     * 0 空闲
     * 1 占用
     * 2 故障
     */
    private Integer status;

    /**
     * 订单号
     */
    private String orderNo;

    public DevicePlusStatusDTO() {
    }

    public DevicePlusStatusDTO(Integer status) {
        this.status = status;
    }
}
