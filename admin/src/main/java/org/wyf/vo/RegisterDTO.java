package org.wyf.vo;

import lombok.Data;

@Data
public class RegisterDTO {

    /**
     *  手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;
    /** 刷新token */
    private String refreshToken;

    /** login / register   */
    private String codeType;



}
