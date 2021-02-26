package org.wyf.vo;

import lombok.Data;

import java.util.Map;

@Data
public class UnifiedorderVO {

    private String orderNo;


    private Map<String,String> payData;

}
