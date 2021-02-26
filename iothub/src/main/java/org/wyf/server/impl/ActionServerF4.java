package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询版本号反馈
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerF4 extends BaseServer {
    @Override
    public String cmd() {
        return "F4";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {
    }
}
