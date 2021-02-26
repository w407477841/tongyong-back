package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询空载断电等待时长反馈
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerE6 extends BaseServer {
    @Override
    public String cmd() {
        return "E6";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {

    }
}
