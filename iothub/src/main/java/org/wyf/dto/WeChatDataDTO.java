package org.wyf.dto;

import lombok.Data;

@Data
public class WeChatDataDTO {

    private String value;

    public WeChatDataDTO(String value) {
        this.value = value;
    }
}
