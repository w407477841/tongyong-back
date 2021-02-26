package org.wyf.vo;

import lombok.Data;

@Data
public class AsIdeaVO {

    private Integer id;

    private String phone;

    private String ideaContent;

    private String feedbackContent;

    private Integer status;

    private Integer feedbackStatus;

    private String comments;
}
