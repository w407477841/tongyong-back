package org.wyf.server.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import org.wyf.common.Const;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerE7 extends BaseServer {
    @Override
    public String cmd() {
        return "E7";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data,DataDTO2 dto2) {

        String  curr = DateUtil.format(new Date(),"yyyyMMddHHmmss");
        StringBuffer sb = new StringBuffer("01");
        for(int i = 0; i<curr.length()/2;i++){
             int timeInt =  Integer.parseInt(curr.substring(i*2,i*2+2));
             String timeHex = StrUtil.fillBefore(Integer.toHexString(timeInt),'0',2) ;
            sb.append(timeHex);
        }



        channel.writeAndFlush(BaseServer.packageResult(data,dto2,sb.toString()));
    }




}
