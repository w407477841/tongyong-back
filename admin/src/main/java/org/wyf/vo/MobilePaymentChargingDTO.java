package org.wyf.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MobilePaymentChargingDTO {


    private String deviceNo;

    private String plug;

    private String orderNo;

    private BigDecimal amount;

    /** 订单状态（0取消支付，1支付成功，2支付失败） */
    private Integer status;

    private String openid;


}
