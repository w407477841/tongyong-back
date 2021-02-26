package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 恢复出厂设置反馈
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerF5 extends BaseServer {
    @Override
    public String cmd() {
        return "F5";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {
    }
}
