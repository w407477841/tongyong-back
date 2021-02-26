package org.wyf.vo;

import lombok.Data;

@Data
public class AsMalfunctionVO {

    private Integer id;

    private String deviceNo;

    private String plugNo;

    private String reportTime;

    private String repairTime;

    private String malfunctionContent;

    private String feedbackContent;

    private Integer status;

    private Integer feedbackStatus;

    private String comments;

    private String phone;
}
