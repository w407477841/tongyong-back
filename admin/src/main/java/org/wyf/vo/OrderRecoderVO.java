package org.wyf.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderRecoderVO {

    /**
     * 系统订单编号
     */
    private String orderNo;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 卡号
     */
    private  String cardNo;

    /**
     * 插头号
     */
    private String plugNo;

    /**
     * 订单标记(1 ：微信支付，2刷卡支付)
     */
    private Integer orderTag;

    /**
     * 合计
     */
    private BigDecimal amount;

    /**
     * 指令状态(0:未下发，1:已下发，2：成功，3：失败)
     */
    private Integer cmdStatus;

    private String chargingStartTime;

    private String chargingEndTime;

    /**
     * 耗时（结束时间-开始时间）
     */
    private String elapsedTime;
}
