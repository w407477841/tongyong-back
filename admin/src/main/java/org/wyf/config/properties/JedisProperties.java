package org.wyf.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 13:41 2018/12/14
 * Modified By : wangyifei
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class JedisProperties {

    private String host;
    private String password;
    private int port;
    private int database;
    private int timeout;



}
