package org.wyf.common;

import org.wyf.properties.NettyProperties;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.wyf.properties.NettyProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 16:26 2019/3/31
 * Modified By : wangyifei
 */
public class SimpleCoderChain implements CoderChain {


    @Override
    public void intProtocolHandler(ChannelPipeline channelPipeline, NettyProperties serverBean) {



        //心跳检测机制  5*60秒没有收到任何数据时,服务器将主动断开连接
        channelPipeline.addLast(new IdleStateHandler(5*60, 0, 0, TimeUnit.SECONDS));
        /*
         * 重要参数说明:
         * 第一个参数:报文允许的最大长度
         * 第二个参数:报文头中标识数据长度的起始位
         * 第三个参数:报文中表示长度所占用的字节偏移量
         * 第四个参数:添加到长度字段的补偿值
         *          如: 原始报文为: 01 02 07 04 05 06 03   报文头为前3个  07表示长度位  表示的是一共7个字节
         *              那么我们如果需要的一个完整报文的话  就需要左偏移 (07的位置(2)+长度所占用的字节数(1))=3
         * 第五个参数:从解码帧中第一次去除的字节数(可用于除去报文头)
         */
      //  channelPipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 2, 9, 2, 0, 0));

      // 固定分隔符结尾
        channelPipeline.addLast(new DelimiterBasedFrameDecoder(1024*2,false,true,Unpooled.copiedBuffer(new byte[]{0x7F})));


    }
}
