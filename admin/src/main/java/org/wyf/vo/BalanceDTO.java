package org.wyf.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDTO {

    private String orderNo;
    private String phone;
    private BigDecimal amount;
    private String openid;
}
