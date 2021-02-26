package org.wyf.config;

import cn.hutool.core.io.FileUtil;
import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import org.wyf.config.properties.WeChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;

@Configuration()
@EnableConfigurationProperties(value={WeChatProperties.class})
@Slf4j
public class WeChatConfig extends WXPayConfig {
    @Autowired
    private WeChatProperties  weChatProperties;

    @Override
    public String getAppID() {
        return weChatProperties.getAppid();
    }
    @Override
    public String getMchID() {
        return weChatProperties.getMchid();
    }
    @Override
    public String getKey() {
        return weChatProperties.getAppkey();
    }
    @Override
    public InputStream getCertStream() {
        String path = System.getProperty("user.dir");
        path.replaceAll("/",File.separator);
        path.replaceAll("\\\\","");
        path = path + File.separator+"apiclient_cert.p12";
        File file = FileUtil.file(path);
        if(!file.exists()){
            return null;
        }
        return FileUtil.getInputStream(file);
    }
    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }
    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
                log.info("上报域名网络状况",ex);
            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }

}
