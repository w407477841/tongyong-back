package org.wyf.server;

import org.wyf.cache.RedisUtil;
import org.wyf.common.Const;
import org.wyf.common.DeviceSerssion;
import org.wyf.common.ProtocolConst;
import org.wyf.common.enums.CmdEnum;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.dto.DeviceDTO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.wyf.cache.RedisUtil;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public abstract class BaseServer implements InitializingBean {

    public static List<BaseServer> services =new ArrayList<>();
    @Autowired
    protected RedisUtil redisUtil;
    @Autowired
    protected RedissonClient redissonClient;



    /**
     *
     * @param cmd 命令码
     * @return
     */
    public boolean supported(String cmd){
        return cmd().equals(cmd);
    }


    /**
     * 支持的命令码
     * @return
     */
    public abstract String cmd();

    protected boolean needLogin(){
        return true;
    }
    public void handler(Channel channel,DataDTO data){
            if(needLogin()){
                // 需要验证登录状态,一般情况，除了登录报文，都需要验证登录状态
                 DeviceSerssion deviceSerssion =  channel.attr(Const.NETTY_CHANNEL_KEY).get();
                 if(deviceSerssion == null){
                    throw new RuntimeException("未登录");
                 }
            }
            String hexStr = data.getData();
            DataDTO2 dto2 = new DataDTO2(hexStr.substring(ProtocolConst.CMD_INDEX[0],ProtocolConst.CMD_INDEX[1]),
                    hexStr.substring(ProtocolConst.PLUS_NO_INDEX[0],ProtocolConst.PLUS_NO_INDEX[1]),
                    hexStr.substring(ProtocolConst.LENGTH_INDEX[1],hexStr.length()-2),
                    Integer.parseInt(hexStr.substring(ProtocolConst.LENGTH_INDEX[0],ProtocolConst.LENGTH_INDEX[1]),16)
                    );
        CmdEnum cmdEnum =  CmdEnum.getEnum(cmd());

        log.info("[{}] "+cmdEnum.getLogfmt()+":[{}][{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),data.toString(),dto2.toString());
            handler0(channel,data,dto2);
    }
    protected abstract void handler0(Channel channel,DataDTO data,DataDTO2 dto2);


    @Override
    public void afterPropertiesSet() throws Exception {
        services.add(this);
    }

    /**
     * 构建返回数据
     * @param dtu  dtu数据体
     * @param dto2  实际数据体
     * @param data  数据段
     * @return
     */
    public static ByteBuf packageResult(DataDTO dtu, DataDTO2 dto2, String data){
        // 构建 实际数据
        DataDTO2 dataDTO2 = new DataDTO2(dto2.getCmd(),dto2.getPlus(),data,null);
        // 构建 dtu数据
        DataDTO dataDTO = new DataDTO(dtu.getCmd(),dtu.getDeviceNo(),dataDTO2.toData(),null);
        // 转义 并 转成ByteBuf
         return Unpooled.copiedBuffer(Const.reTranslation(dataDTO.toData()));

    }

    /**
     * 远程方法调用中间过程
     */
    public void remoteProcess(DeviceDTO deviceDTO){


    }

    /**
     * 更新
     */
    public void update(DeviceDTO deviceDTO){


    }


}
