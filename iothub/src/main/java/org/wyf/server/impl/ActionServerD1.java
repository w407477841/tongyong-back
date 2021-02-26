package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设置浮充充电站功率值反馈
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerD1 extends BaseServer {
    @Override
    public String cmd() {
        return "D1";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {

    }
}
