package org.wyf.common;

import org.wyf.properties.NettyProperties;
import io.netty.channel.ChannelPipeline;
import org.wyf.properties.NettyProperties;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 16:23 2019/3/31
 * Modified By : wangyifei
 */
public interface CoderChain {

 void intProtocolHandler(ChannelPipeline channelPipeline, NettyProperties serverBean);

}
