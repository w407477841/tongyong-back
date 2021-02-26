package org.wyf.handler;
import	java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.common.Const;
import org.wyf.common.DeviceSerssion;
import org.wyf.common.ProtocolConst;
import org.wyf.common.enums.CmdEnum;
import org.wyf.dto.DataDTO;
import org.wyf.server.BaseServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.OrientationRequested;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 15:33 2019/3/29
 * Modified By : wangyifei
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class SimpleHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Autowired
    private IBasicPileService basicPileService;




    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {

        Channel channel =  channelHandlerContext. channel();

        //收到的原始数据串

        int len = msg.readableBytes();
        //  可读开始位置
        byte[] msgData = new byte[len];
        msg.readBytes(msgData);

        log.info("[{}] 转译前[{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),Const.toHexString(msgData));
        // 组装 DTU 数据
        DataDTO dataDTO =   packageDTU(msgData);

        // 没有登录报文，随便来一条认为就是登录
        // 需要验证登录状态,一般情况，除了登录报文，都需要验证登录状态
        DeviceSerssion deviceSerssion = channel.attr(Const.NETTY_CHANNEL_KEY).get();
        if(deviceSerssion == null){
            log.info("[{}] 设备登录:[{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),dataDTO.getDeviceNo());
                // 这是个新的链接
                String deviceNo = dataDTO.getDeviceNo();
                Channel oldChannel = Const.CHANNEL_MAP.get(deviceNo);
                if(oldChannel!=null){
                    // 该设备存在还未断开的链路,将当前链路断开，并将之前的链路断开
                    oldChannel.close();
                    channel.close();
                    return ;
                }else {
                    // 这个登录毫无问题

                    BasicPile basicPile =   basicPileService.getOne(new QueryWrapper<BasicPile>().eq("is_del",0).eq("device_no",deviceNo));
                    if(basicPile == null){
                        // 设备未添加
                     log.error("[{}] 设备不存在[{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),deviceNo);
                        channel.close();
                        return ;
                    }
                    DeviceSerssion ds = new DeviceSerssion();
                    ds.setDeviceNo(dataDTO.getDeviceNo());
                    channel.attr(Const.NETTY_CHANNEL_KEY).set(ds);
                    Const.CHANNEL_MAP.put(deviceNo,channel);
                    basicPile.setOnline(1);
                    basicPileService.updateById(basicPile);

                }

        }


            // 判断是心跳还是
            if("00BB".equals(dataDTO.getCmd())){
                // 心跳不做任何处理
                log.debug("[{}] 心跳[{}] [{}]",dataDTO.getCmd(),channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),Const.toHexString(msgData));
                channel.writeAndFlush(Unpooled.copiedBuffer(msgData));
            } else if ("00AB".equals(dataDTO.getCmd())) {
                // 透传数据 实际设备数据
                log.debug("[{}] 透传[{}] [{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),dataDTO.getCmd(),Const.toHexString(msgData));
                BaseServer.services.stream().filter(p->
                     p.supported(dataDTO.getData().substring(6,8))
                ).forEach(item->{
                    item.handler(channel,dataDTO);
                });

            }else{
                log.debug("[{}] 未知[{}] [{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),dataDTO.getCmd(),Const.toHexString(msgData));
            }

        }


    /**
     * 将dtu数据组装成实体类
     * @param oriData
     * @return
     */
    private  DataDTO packageDTU(byte[] oriData){


        byte[] msgData = HexUtil.decodeHex(Const.translation(oriData)) ;

        // 组装 DTU 数据
        byte[]  snArray = new byte[5];
        System.arraycopy(msgData,ProtocolConst.PROTOCOL_SN_INDEXS[0],snArray,0,5);
        String sn =  Const.toHexString(snArray);

        byte[]  funcationArray = new byte[2];
        System.arraycopy(msgData,ProtocolConst.PROTOCOL_FUNCTION_INDEXS[0],funcationArray,0,2);
        String functionCode =  Const.toHexString(funcationArray);

        byte[]  lengthArray = new byte[2];
        System.arraycopy(msgData,ProtocolConst.PROTOCOL_LENGTH_INDEXS[0],lengthArray,0,2);
        Integer length =  Integer.parseInt(Const.toHexString(lengthArray),16) ;


        byte[]  dataArray = new byte[length-2];
        System.arraycopy(msgData,ProtocolConst.PROTOCOL_LENGTH_INDEXS[1],dataArray,0,length-2);
        String data =  Const.toHexString(dataArray) ;


        DataDTO dataDTO = new DataDTO(functionCode,sn,data,length);
        return dataDTO;
    }





    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).set(RandomUtil.simpleUUID());
        SocketAddress socketAddress =  ctx.channel().remoteAddress() ;
        Const.CURRENT_LINKS.increment();
        log.info("[{}] 新建连接:[{}],当前总连接数:[{}]",ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(),socketAddress.toString(),Const.CURRENT_LINKS.longValue());



    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel =  ctx. channel();
        SocketAddress socketAddress =  ctx.channel().remoteAddress() ;
        Const.CURRENT_LINKS.decrement();
        DeviceSerssion deviceSerssion = channel.attr(Const.NETTY_CHANNEL_KEY).get();
        if(deviceSerssion != null){
            String deviceNo = deviceSerssion.getDeviceNo();
                log.info("[{}] 设备登出:[{}]",ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(),deviceNo);
                BasicPile basicPile =   basicPileService.getOne(new QueryWrapper<BasicPile>().eq("is_del",0).eq("device_no",deviceNo));
                if(basicPile == null){
                    // 设备未添加
                    log.error("[{}] 设备不存在[{}]",ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(),deviceNo);
                    return ;
                }else{
                    basicPile.setOnline(0);
                    basicPileService.updateById(basicPile);

                }
            Const.CHANNEL_MAP.remove(deviceNo);
        }
        log.info("[{}] 断开连接:[{}],当前总连接数:[{}]",ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(),socketAddress.toString(),Const.CURRENT_LINKS.longValue());
    }

    /**
     * 事件触发后执行
     * 配合 IdleStateHandler使用
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("[{}] 心跳超时:[{}]", ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(),ctx.channel().remoteAddress());
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                /*写超时*/
                // System.out.println("===服务端===(WRITER_IDLE 写超时)");
            } else if (event.state() == IdleState.ALL_IDLE) {
                /*总超时*/
                // System.out.println("===服务端===(ALL_IDLE 总超时)");
            }
        }
    }

    /**
     *  异常处理
     *  打印异常，关闭连接
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("[{}] 发生异常:[{}],信息:[{}] ",ctx.channel().attr(Const.NETTY_CHANNEL_UUID_KEY).get(), ctx.channel().remoteAddress(),cause);
        cause.printStackTrace();
        ctx.close();
    }
}
