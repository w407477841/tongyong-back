package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询充满等待断电时长 反馈
 */
@Slf4j
@Component
public class ActionServerB2 extends BaseServer {
    @Override
    public String cmd() {
        return "B2";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {
    }
}
