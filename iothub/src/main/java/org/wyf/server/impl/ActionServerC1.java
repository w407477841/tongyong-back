package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询投币消费总金额反馈
 * @author wangyifei
 */
@Slf4j
@Component
public class ActionServerC1 extends BaseServer {
    @Override
    public String cmd() {
        return "C1";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data,DataDTO2 dto2) {


    }
}
