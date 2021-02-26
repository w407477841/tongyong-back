package org.wyf.server.impl;

import org.wyf.common.Const;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 实体卡上报
 * @author wangyifei
 */
@Slf4j
@Component
public class ActionServerB6 extends BaseServer {
    @Override
    public String cmd() {
        return "B6";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data,DataDTO2 dto2) {



        channel.writeAndFlush(BaseServer.packageResult(data,dto2,"01"));
        // 失败
        //channel.writeAndFlush(BaseServer.packageResult(data,dto2,"00"));
    }
}
