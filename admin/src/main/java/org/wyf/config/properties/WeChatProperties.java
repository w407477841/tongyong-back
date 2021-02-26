package org.wyf.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wechat")
@Data
public class WeChatProperties  {


    private String secret ;

    private String jscode2sessionUrl;

    private boolean useSandbox;

    private  String appid;

    private String mchid;

    private String appkey;




}
