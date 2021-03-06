package org.wyf.server.impl;

import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 虚拟卡工作完成上报订单信息
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerC8 extends BaseServer {
    @Override
    public String cmd() {
        return "C8";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {

        channel.writeAndFlush(BaseServer.packageResult(data,dto2,"01"));
        // 失败
        //channel.writeAndFlush(BaseServer.packageResult(data,dto2,"00"));


    }
}
